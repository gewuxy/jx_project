package lib.ys.ui.interfaces.impl;

import android.util.SparseArray;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import lib.network.Network;
import lib.network.model.NetworkError;
import lib.network.model.NetworkErrorBuilder;
import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.ui.interfaces.opt.INetworkOpt;
import lib.ys.util.DeviceUtil;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * @auther yuansui
 * @since 2017/5/8
 */

public class NetworkOpt implements INetworkOpt {

    private Network mNetwork;
    private OnNetworkListener mNetworkLsn;
    private Object mHost;
    private SparseArray<NetworkReq> mMapRetryTask;


    public NetworkOpt(Object host, OnNetworkListener networkLsn) {
        mNetworkLsn = networkLsn;
        mHost = host;

        mMapRetryTask = new SparseArray<>();
    }

    @Override
    public void exeNetworkReq(NetworkReq req) {
        exeNetworkReq(KDefaultId, req);
    }

    @Override
    public void exeNetworkReq(int id, NetworkReq req) {
        exeNetworkReq(id, req, mNetworkLsn);
    }

    @Override
    public void exeNetworkReq(int id, NetworkReq req, OnNetworkListener l) {
        if (req == null) {
            mNetworkLsn.onNetworkError(id, new NetworkError());
            return;
        }

        if (req.getRetry() != null) {
            mMapRetryTask.put(id, req);
        }

        if (!DeviceUtil.isNetworkEnabled()) {
            mNetworkLsn.onNetworkError(id, NetworkErrorBuilder.create().message(Network.getConfig().getDisconnectToast()).build());
            return;
        }

        if (mNetwork == null) {
            mNetwork = new Network(mHost.getClass().getName(), mNetworkLsn);
        }
        mNetwork.load(id, req, l);
    }

    @Override
    public WebSocket exeWebSocketReq(NetworkReq req, WebSocketListener l) {
        if (mNetwork == null) {
            mNetwork = new Network(mHost.getClass().getName(), mNetworkLsn);
        }
        return mNetwork.loadWebSocket(req, l);
    }

    @Override
    public void cancelAllNetworkReq() {
        if (mNetwork != null) {
            mNetwork.cancelAll();
        }
    }

    @Override
    public void cancelNetworkReq(int id) {
        if (mNetwork != null) {
            mNetwork.cancel(id);
        }
    }

    public boolean retryNetworkRequest(int id) {
        NetworkReq request = mMapRetryTask.get(id);
        if (request == null) {
            return false;
        }

        if (request.getRetry().reduce()) {
            Observable.timer(request.getRetry().getDelay(), TimeUnit.MILLISECONDS)
                    .subscribe(aLong -> exeNetworkReq(id, request));
        } else {
            mMapRetryTask.remove(id);
            return false;
        }

        return true;
    }

    public void onDestroy() {
        cancelAllNetworkReq();

        if (mNetwork != null) {
            mNetwork.destroy();
            mNetwork = null;
        }

        mMapRetryTask.clear();
    }
}

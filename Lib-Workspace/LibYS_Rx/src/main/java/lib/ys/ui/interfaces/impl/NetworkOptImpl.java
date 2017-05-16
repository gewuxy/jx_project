package lib.ys.ui.interfaces.impl;

import android.util.SparseArray;

import lib.network.Network;
import lib.network.error.ConnectionNetError;
import lib.network.error.NetError;
import lib.network.model.NetworkReq;
import lib.network.model.OnNetworkListener;
import lib.ys.R;
import lib.ys.ui.interfaces.opts.NetworkOpt;
import lib.ys.util.DeviceUtil;
import lib.ys.util.res.ResLoader;


/**
 * @auther yuansui
 * @since 2017/5/8
 */

public class NetworkOptImpl implements NetworkOpt {

    private Network mNetwork;
    private OnNetworkListener mNetworkLsn;
    private Object mHost;
    private SparseArray<NetworkReq> mMapRetryTask;


    public NetworkOptImpl(Object host, OnNetworkListener networkLsn) {
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
            mNetworkLsn.onNetworkError(id, new NetError());
            return;
        }

        if (req.getRetry() != null) {
            mMapRetryTask.put(id, req);
        }

        if (!DeviceUtil.isNetworkEnable()) {
            mNetworkLsn.onNetworkError(id, new ConnectionNetError(ResLoader.getString(R.string.toast_network_disconnect)));
            return;
        }

        if (mNetwork == null) {
            mNetwork = new Network(mHost.getClass().getName(), mNetworkLsn);
        }
        mNetwork.execute(id, req, l);
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
            exeNetworkReq(id, request);
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

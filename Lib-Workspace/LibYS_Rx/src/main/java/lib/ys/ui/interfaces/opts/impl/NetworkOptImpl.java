package lib.ys.ui.interfaces.opts.impl;

import android.util.SparseArray;

import lib.network.Network;
import lib.network.error.ConnectionNetError;
import lib.network.error.NetError;
import lib.network.model.NetworkRequest;
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
    private SparseArray<NetworkRequest> mMapRetryTask;


    public NetworkOptImpl(Object host, OnNetworkListener networkLsn) {
        mNetworkLsn = networkLsn;
        mHost = host;

        mMapRetryTask = new SparseArray<>();
    }

    @Override
    public void exeNetworkRequest(int id, NetworkRequest request) {
        exeNetworkRequest(id, request, mNetworkLsn);
    }

    @Override
    public void exeNetworkRequest(int id, NetworkRequest request, OnNetworkListener listener) {
        if (request == null) {
            mNetworkLsn.onNetworkError(id, new NetError());
            return;
        }

        if (request.getRetry() != null) {
            mMapRetryTask.put(id, request);
        }

        if (!DeviceUtil.isNetworkEnable()) {
            mNetworkLsn.onNetworkError(id, new ConnectionNetError(ResLoader.getString(R.string.toast_network_disconnect)));
            return;
        }

        if (mNetwork == null) {
            mNetwork = new Network(mHost.getClass().getName(), mNetworkLsn);
        }
        mNetwork.execute(id, request, listener);
    }

    @Override
    public void cancelAllNetworkRequest() {
        if (mNetwork != null) {
            mNetwork.cancelAll();
        }
    }

    @Override
    public void cancelNetworkRequest(int id) {
        if (mNetwork != null) {
            mNetwork.cancel(id);
        }
    }

    public boolean retryNetworkRequest(int id) {
        NetworkRequest request = mMapRetryTask.get(id);
        if (request == null) {
            return false;
        }

        if (request.getRetry().reduce()) {
            exeNetworkRequest(id, request);
        } else {
            mMapRetryTask.remove(id);
            return false;
        }

        return true;
    }

    public void onDestroy() {
        if (mNetwork != null) {
            mNetwork.destroy();
            mNetwork = null;
        }

        mMapRetryTask.clear();
    }
}

package lib.ys.ui.interfaces.opts.impl;

import lib.network.NetworkExecutor;
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

    private NetworkExecutor mNetworkExecutor;
    private OnNetworkListener mNetworkLsn;
    private Object mHost;

    public NetworkOptImpl(Object host, OnNetworkListener networkLsn) {
        mNetworkLsn = networkLsn;
        mHost = host;
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

        if (!DeviceUtil.isNetworkEnable()) {
            mNetworkLsn.onNetworkError(id, new ConnectionNetError(ResLoader.getString(R.string.toast_network_disconnect)));
            return;
        }

        if (mNetworkExecutor == null) {
            mNetworkExecutor = new NetworkExecutor(mHost.getClass().getName(), mNetworkLsn);
        }
        mNetworkExecutor.execute(id, request, listener);
    }

    @Override
    public void cancelAllNetworkRequest() {
        if (mNetworkExecutor != null) {
            mNetworkExecutor.cancelAll();
        }
    }

    @Override
    public void cancelNetworkRequest(int id) {
        if (mNetworkExecutor != null) {
            mNetworkExecutor.cancel(id);
        }
    }

    public void onDestroy() {
        if (mNetworkExecutor != null) {
            mNetworkExecutor.destroy();
            mNetworkExecutor = null;
        }
    }

}

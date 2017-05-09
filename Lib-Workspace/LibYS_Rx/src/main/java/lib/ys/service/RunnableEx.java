package lib.ys.service;

import org.json.JSONException;

import lib.network.Network;
import lib.network.error.NetError;
import lib.network.model.OnNetworkListener;
import lib.network.model.NetworkRequest;
import lib.network.model.NetworkResponse;
import lib.ys.LogMgr;
import lib.ys.ui.interfaces.opts.NetworkOpt;

/**
 * @author yuansui
 */
abstract public class RunnableEx implements Runnable, NetworkOpt, OnNetworkListener {

    protected final String TAG = getClass().getSimpleName();

    private Network mNetwork;

    /**
     * http task part
     */

    public void exeNetworkRequest(int id, NetworkRequest request) {
        exeNetworkRequest(id, request, this);
    }

    public void exeNetworkRequest(int id, NetworkRequest request, OnNetworkListener listener) {
        if (mNetwork == null) {
            mNetwork = new Network(getClass().getName(), this);
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

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws JSONException {
        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        LogMgr.d(TAG, "onNetworkError(): " + "what = [" + id + "], error = [" + error.getMessage() + "]");
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
    }

}

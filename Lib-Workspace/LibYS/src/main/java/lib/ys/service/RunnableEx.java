package lib.ys.service;

import org.json.JSONException;

import lib.network.NetworkExecutor;
import lib.network.error.NetError;
import lib.network.model.NetworkRequest;
import lib.network.model.NetworkResponse;
import lib.network.model.OnNetworkListener;
import lib.ys.LogMgr;
import lib.ys.interfaces.INetwork;

/**
 * @author yuansui
 */
abstract public class RunnableEx implements Runnable, INetwork {

    protected final String TAG = getClass().getSimpleName();

    private NetworkExecutor mNetworkExecutor;

    /**
     * http task part
     */

    public void exeNetworkRequest(int id, NetworkRequest request) {
        exeNetworkRequest(id, request, this);
    }

    public void exeNetworkRequest(int id, NetworkRequest request, OnNetworkListener listener) {
        if (mNetworkExecutor == null) {
            mNetworkExecutor = new NetworkExecutor(getClass().getName(), this);
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

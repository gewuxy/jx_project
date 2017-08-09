package lib.ys.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONException;

import lib.ys.util.InjectUtil;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.YSLog;
import lib.ys.ui.interfaces.impl.NetworkOpt;
import lib.ys.ui.interfaces.opt.INetworkOpt;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


abstract public class ServiceEx extends Service implements INetworkOpt, OnNetworkListener {

    protected final String TAG = getClass().getSimpleName();

    private NetworkOpt mNetworkImpl;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public final void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent != null) {
            InjectUtil.bind(this, intent);
            onHandleIntent(intent);
        }
    }

    abstract protected void onHandleIntent(Intent intent);

    /**
     * http task part
     */

    @Override
    public void exeNetworkReq(NetworkReq req) {
        exeNetworkReq(KDefaultId, req);
    }

    @Override
    public void exeNetworkReq(int id, NetworkReq req) {
        exeNetworkReq(id, req, this);
    }

    @Override
    public void exeNetworkReq(int id, NetworkReq req, OnNetworkListener l) {
        if (mNetworkImpl == null) {
            mNetworkImpl = new NetworkOpt(this, this);
        }
        mNetworkImpl.exeNetworkReq(id, req, l);
    }

    @Override
    public WebSocket exeWebSocketReq(NetworkReq req, WebSocketListener l) {
        if (mNetworkImpl == null) {
            mNetworkImpl = new NetworkOpt(this, this);
        }
        return mNetworkImpl.exeWebSocketReq(req, l);
    }

    @Override
    public void cancelAllNetworkReq() {
        if (mNetworkImpl != null) {
            mNetworkImpl.cancelAllNetworkReq();
        }
    }

    @Override
    public void cancelNetworkReq(int id) {
        if (mNetworkImpl != null) {
            mNetworkImpl.cancelNetworkReq(id);
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws JSONException {
        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        Exception e = error.getException();
        if (e != null) {
            YSLog.d(TAG, "onNetworkError: id = " + id);
            YSLog.d(TAG, "onNetworkError: e = " + e.getMessage());
            YSLog.d(TAG, "onNetworkError: msg = " + error.getMessage());
            YSLog.d(TAG, "onNetworkError: end=======================");
        } else {
            YSLog.d(TAG, "onNetworkError(): " + "tag = [" + id + "], error = [" + error.getMessage() + "]");
        }

        retryNetworkRequest(id);
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
    }

    protected boolean retryNetworkRequest(int id) {
        if (mNetworkImpl != null) {
            return mNetworkImpl.retryNetworkRequest(id);
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mNetworkImpl != null) {
            mNetworkImpl.onDestroy();
            mNetworkImpl = null;
        }
    }

    protected void startService(Class<? extends Service> clz) {
        startService(new Intent(this, clz));
    }
}

package yy.doctor.ui.activity.meeting.play;

import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.ui.interfaces.impl.NetworkOpt;
import lib.ys.ui.interfaces.opt.INetworkOpt;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @auther : GuoXuan
 * @since : 2017/9/27
 */

public class BasePresenterImpl implements BasePresenter, OnNetworkListener {

    protected String TAG = getClass().getSimpleName();

    private NetworkOpt mNetworkImpl;

    public void exeNetworkReq(NetworkReq req) {
        exeNetworkReq(INetworkOpt.KDefaultId, req);
    }

    protected void exeNetworkReq(int id, NetworkReq req) {
        if (mNetworkImpl == null) {
            mNetworkImpl = new NetworkOpt(this, this);
        }
        mNetworkImpl.exeNetworkReq(id, req, this);
    }

    protected WebSocket exeWebSocketReq(NetworkReq req, WebSocketListener l) {
        if (mNetworkImpl == null) {
            mNetworkImpl = new NetworkOpt(this, this);
        }
        return mNetworkImpl.exeWebSocketReq(req, l);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
    }

    public void onDestroy() {
        mNetworkImpl.onDestroy();
        mNetworkImpl = null;
    }
}

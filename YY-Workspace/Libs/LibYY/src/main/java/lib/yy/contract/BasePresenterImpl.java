package lib.yy.contract;

import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.AppEx;
import lib.ys.YSLog;
import lib.ys.ui.interfaces.impl.NetworkOpt;
import lib.ys.ui.interfaces.opt.INetworkOpt;
import lib.yy.network.BaseJsonParser;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @auther : GuoXuan
 * @since : 2017/9/27
 */
abstract public class BasePresenterImpl<V extends IContract.View> implements
        IContract.Presenter<V>,
        OnNetworkListener {

    protected final String TAG = getClass().getSimpleName();

    private V mV;

    private NetworkOpt mNetworkImpl;

    public BasePresenterImpl(V v) {
        mV = v;
    }

    @Override
    public V getView() {
        return mV;
    }

    @Override
    public void exeNetworkReq(NetworkReq req) {
        exeNetworkReq(INetworkOpt.KDefaultId, req);
    }

    @Override
    public void exeNetworkReq(int id, NetworkReq req) {
        if (mNetworkImpl == null) {
            mNetworkImpl = new NetworkOpt(this, this);
        }
        mNetworkImpl.exeNetworkReq(id, req, this);
    }

    @Override
    public WebSocket exeWebSocketReq(NetworkReq req, WebSocketListener l) {
        if (mNetworkImpl == null) {
            mNetworkImpl = new NetworkOpt(this, this);
        }
        return mNetworkImpl.exeWebSocketReq(req, l);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        // 只解析错误信息
        return BaseJsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
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
        AppEx.showToast(error.getMessage());
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
    }

    public boolean retryNetworkRequest(int id) {
        if (mNetworkImpl != null) {
            return mNetworkImpl.retryNetworkRequest(id);
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mNetworkImpl.onDestroy();
        mNetworkImpl = null;
    }
}

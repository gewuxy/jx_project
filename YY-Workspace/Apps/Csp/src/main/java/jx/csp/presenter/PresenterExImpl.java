package jx.csp.presenter;

import jx.csp.ui.ViewEx;
import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.ui.interfaces.impl.NetworkOpt;
import lib.ys.ui.interfaces.opt.INetworkOpt;

/**
 * @auther yuansui
 * @since 2017/10/24
 */

abstract public class PresenterExImpl<V extends ViewEx> implements PresenterEx, OnNetworkListener{

    private V mV;

    public PresenterExImpl(V v) {
        mV = v;
    }

    protected V getView() {
        return mV;
    }

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

    @Override
    public void onDestroy() {
        mNetworkImpl.onDestroy();
        mNetworkImpl = null;
    }
}

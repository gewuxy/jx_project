package jx.csp.presenter.star;

import android.support.annotation.CallSuper;

import jx.csp.contact.StarContract;
import jx.csp.model.main.Meet;
import jx.csp.model.meeting.Code;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor;
import lib.jx.contract.BasePresenterImpl;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx;

/**
 * @auther : GuoXuan
 * @since : 2018/1/19
 */
 class BaseStarPresenterImpl<V extends StarContract.V> extends BasePresenterImpl<V> implements StarContract.P<V> {

    private Meet mMeet;

    public Meet getMeet() {
        return mMeet;
    }

     BaseStarPresenterImpl(V v, Meet meet) {
        super(v);
        mMeet = meet;
    }

    @Override
    public void getDataFromNet(int netType) {
        if (netType == StarContract.KReqStar) {
            String courseId = mMeet.getString(Meet.TMeet.id);
            exeNetworkReq(netType, NetworkApiDescriptor.MeetingAPI.code(courseId).build());
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == StarContract.KReqStar) {
            return JsonParser.ev(resp.getText(), Code.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @CallSuper
    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onNetworkSuccess(id, r);
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        getView().setViewState(DecorViewEx.ViewState.error);
    }
}

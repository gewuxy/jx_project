package jx.csp.presenter;

import jx.csp.contact.StartContract;
import jx.csp.model.main.Meet;
import jx.csp.model.meeting.Code;
import jx.csp.model.meeting.Course;
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
public class StartPresenterImpl extends BasePresenterImpl<StartContract.V> implements StartContract.P {

    private Meet mMeet;

    public StartPresenterImpl(StartContract.V v, Meet meet) {
        super(v);
        mMeet = meet;
    }

    @Override
    public void setPlayState() {
        boolean reb = mMeet.getInt(Meet.TMeet.playType) == Course.CourseType.reb;
        getView().setReb(reb);
    }

    @Override
    public void getDataFromNet() {
        String courseId = mMeet.getString(Meet.TMeet.id);
        exeNetworkReq(NetworkApiDescriptor.MeetingAPI.code(courseId).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Code.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            Code c = (Code) r.getData();
            if (c == null) {
                return;
            }
            getView().onNetworkSuccess(c);
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        getView().setViewState(DecorViewEx.ViewState.error);
    }
}

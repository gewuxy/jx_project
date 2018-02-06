package jx.csp.presenter;

import jx.csp.contact.StarContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Code;
import jx.csp.model.meeting.Course;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.jx.contract.BasePresenterImpl;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx;

/**
 * @auther : GuoXuan
 * @since : 2018/1/19
 */
public class StarPresenterImpl extends BasePresenterImpl<StarContract.V> implements StarContract.P {

    private final int KCodeReqId = 0;
    private final int KDeleteBgMusicReqId = 0;
    private Meet mMeet;

    public StarPresenterImpl(StarContract.V v, Meet meet) {
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
        exeNetworkReq(KCodeReqId, NetworkApiDescriptor.MeetingAPI.code(courseId).build());
    }

    @Override
    public void deleteBgMusic() {
        exeNetworkReq(KDeleteBgMusicReqId, MeetingAPI.updateMini(mMeet.getInt(TMeet.id), mMeet.getString(TMeet.title)).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KCodeReqId) {
            return JsonParser.ev(resp.getText(), Code.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KCodeReqId) {
            if (r.isSucceed()) {
                Code c = (Code) r.getData();
                if (c == null) {
                    return;
                }
                getView().onNetworkSuccess(c);
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            if (r.isSucceed()) {
                getView().deleteBgMusicSuccess();
            } else {
                retryNetworkRequest(id);
            }
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        getView().setViewState(DecorViewEx.ViewState.error);
    }
}

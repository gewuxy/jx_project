package yy.doctor.ui.activity.meeting.play.presenter;

import android.os.Handler;
import android.os.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.TextUtil;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.network.Result;
import okhttp3.WebSocket;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.TCourse;
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;
import yy.doctor.ui.activity.meeting.play.MeetWebSocketListener;
import yy.doctor.ui.activity.meeting.play.contract.MeetingLiveContract;
import yy.doctor.util.NetPlayer;
import yy.doctor.util.Time;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */

public class MeetingLivePresenterImpl extends BasePresenterImpl<MeetingLiveContract.View> implements MeetingLiveContract.Presenter, NetPlayer.OnPlayerListener {

    private PPT mPpt;
    private WebSocket mWebSocket;
    private long mAllMillisecond;

    private Handler mHandler;
    private List<Course> mCourses;

    public MeetingLivePresenterImpl(MeetingLiveContract.View view) {
        super(view);
        NetPlayer.inst().setListener(this);
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                getView().finishCount();
            }

        };
    }

    @Override
    public void getDataFromNet(String meetId, String moduleId) {
        exeNetworkReq(MeetAPI.toCourse(meetId, moduleId).build());
    }

    @Override
    public void starCount() {
//        mHandler.removeMessages(0);
//        mHandler.sendEmptyMessageDelayed(0, TimeUnit.SECONDS.toMillis(3));
    }

    @Override
    public void mediaStop() {
        mHandler.removeMessages(0);
        NetPlayer.inst().pause();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), PPT.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        getView().onStopRefresh();
        Result<PPT> r = (Result<PPT>) result;
        if (r.isSucceed()) {
            mPpt = r.getData();
            getView().initView(mPpt);
            if (mPpt == null) {
                return;
            }
            String url = mPpt.getString(TPPT.socketUrl);
            if (TextUtil.isEmpty(url)) {
                return;
            }
            mWebSocket = exeWebSocketReq(NetFactory.webLive(url), new WebSocketImpl());
        } else {
            onNetworkError(id, r.getError());
            getView().showToast(r.getMessage());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        getView().onStopRefresh();
        getView().setViewState(ViewState.error);
    }

    @Override
    public void onDownProgress(int progress) {

    }

    @Override
    public void onPreparedSuccess(long allMillisecond) {
        mAllMillisecond = allMillisecond;
        getView().getPptFrag().mediaVisibility(true);
        getView().getPptFrag().animation(true);
    }

    @Override
    public void onPreparedError() {

    }

    @Override
    public void onProgress(long currMilliseconds, int progress) {
        getView().getPptFrag().setTextMedia(Time.getTime(mAllMillisecond - currMilliseconds));
    }

    @Override
    public void onPlayState(boolean state) {
        getView().getPptFrag().animation(state);
    }

    @Override
    public void onCompletion() {
        getView().getPptFrag().animation(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWebSocket != null) {
            mWebSocket.close(1000, "close");
        }
        mHandler.removeCallbacksAndMessages(null);
        NetPlayer.inst().recycle();

    }

    public class WebSocketImpl extends MeetWebSocketListener {

        @Override
        protected void online(int onlineNum) {
            getView().setTextOnline(onlineNum);
        }

        @Override
        public void onMessage(int order, int index, Course course) {
            if (order == OrderType.live) {
                // 直播
                if (mCourses == null) {
                    CourseInfo courseInfo = mPpt.get(TPPT.course);
                    if (courseInfo == null) {
                        return;
                    }

                    mCourses = courseInfo.getList(CourseInfo.TCourseInfo.details);
                    if (mCourses == null) {
                        return;
                    }
                }

                if (index > mCourses.size() - 1) {
                    return;
                }

                Course c = mCourses.get(index);
                c.put(TCourse.audioUrl, course.getString(TCourse.audioUrl));
                getView().addCourse(null, index);
            } else if (order == OrderType.synchronize) {
                // 同步
                getView().addCourse(course, index);
            }
        }

        @Override
        public void reconnect() {
            if (mPpt != null) {
                String url = mPpt.getString(TPPT.socketUrl);
                if (mWebSocket == null) {
                    mWebSocket = exeWebSocketReq(NetFactory.webLive(url), new WebSocketImpl());
                }
            }
        }

    }
}

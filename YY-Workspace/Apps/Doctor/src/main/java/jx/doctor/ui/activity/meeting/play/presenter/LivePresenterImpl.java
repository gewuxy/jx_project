package jx.doctor.ui.activity.meeting.play.presenter;

import android.os.Handler;
import android.os.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.TCourse;
import jx.doctor.model.meet.ppt.CourseInfo;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.model.meet.ppt.PPT.TPPT;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetFactory;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.ui.activity.meeting.play.MeetWebSocketListener;
import jx.doctor.ui.activity.meeting.play.contract.LiveContract;
import jx.doctor.util.NetPlayer;
import lib.jx.contract.BasePresenterImpl;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.TextUtil;
import okhttp3.WebSocket;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */

public class LivePresenterImpl extends BasePresenterImpl<LiveContract.View> implements
        LiveContract.Presenter,
        NetPlayer.OnPlayerListener {

    private PPT mPpt;
    private WebSocket mWebSocket;
    private long mAllMillisecond;

    private Handler mHandler;
    private List<Course> mCourses;

    public LivePresenterImpl(LiveContract.View view) {
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
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, TimeUnit.SECONDS.toMillis(3));
    }

    @Override
    public void stopCount() {
        mHandler.removeMessages(0);
    }

    @Override
    public void mediaStop() {
        mHandler.removeMessages(0);
        NetPlayer.inst().pause();
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), PPT.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (r.isSucceed()) {
            mPpt = (PPT) r.getData();
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
    }

    @Override
    public void onPreparedError() {

    }

    @Override
    public void onProgress(long currMilliseconds, int progress) {
//        getView().getPptFrag().setTextMedia(Time.getTime(mAllMillisecond - currMilliseconds));
    }

    @Override
    public void onPlayState(boolean state) {
    }

    @Override
    public void onCompletion() {
        getView().nextItem();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MeetWebSocketListener.close(mWebSocket);
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
                c.put(TCourse.imgUrl, course.getString(TCourse.imgUrl));
                c.put(TCourse.temp, false); // 已经换了
                getView().refresh(c);
            } else if (order == OrderType.synchronize) {
                // 同步
                getView().addCourse(course);
            }
        }

        @Override
        protected void startPull() {
            getView().startPull();
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

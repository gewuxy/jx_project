package yy.doctor.ui.activity.meeting.play.presenter;

import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import okhttp3.WebSocket;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.TCourse;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.meeting.play.MeetWebSocketListener;
import yy.doctor.ui.activity.meeting.play.contract.PptLiveContract;
import yy.doctor.util.NetPlayer;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */
public class PptLivePresenterImpl extends BasePptPresenterImpl<PptLiveContract.View> implements PptLiveContract.Presenter {

    private WebSocket mWebSocket;

    private boolean mPlay; // 播放声音

    public PptLivePresenterImpl(PptLiveContract.View view) {
        super(view);

        mPlay = true;
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        super.onNetworkSuccess(id, r);

        if (mPpt == null) {
            return;
        }
        String url = mPpt.getString(TPPT.socketUrl);
        if (TextUtil.isEmpty(url)) {
            return;
        }
        YSLog.d(TAG, "onNetworkSuccess:" + url);
        mWebSocket = exeWebSocketReq(NetFactory.webLive(url), new WebSocketImpl());
    }

    @Override
    public void toggle(int index) {
        super.toggle(index);

        if (mPlay) {
            mPlay = NetPlayer.inst().closeVolume();
        } else {
            mPlay = NetPlayer.inst().openVolume();
        }
        getView().onPlayState(mPlay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MeetWebSocketListener.close(mWebSocket);
    }

    public class WebSocketImpl extends MeetWebSocketListener {

        @Override
        protected void online(int onlineNum) {
            getView().setTextOnline(onlineNum);
        }

        @Override
        public void onMessage(int order, int index, Course course) {
            if (order == OrderType.live) {
                // 直播(音频)
                if (mCourses == null || index > mCourses.size() - 1) {
                    return;
                }
                Course c = mCourses.get(index);
                c.put(TCourse.audioUrl, course.getString(TCourse.audioUrl));
                c.put(TCourse.imgUrl, course.getString(TCourse.imgUrl));
                c.put(TCourse.temp, false); // 已经换了
                getView().refresh(c);
            } else if (order == OrderType.synchronize) {
                // 同步(图片,视频)
                getView().addCourse(course);
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

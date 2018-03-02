package jx.doctor.ui.activity.meeting.play.presenter;

import java.util.List;

import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.TCourse;
import jx.doctor.model.meet.ppt.PPT.TPPT;
import jx.doctor.network.NetFactory;
import jx.doctor.ui.activity.meeting.play.MeetWebSocketListener;
import jx.doctor.ui.activity.meeting.play.contract.LiveContract1;
import lib.network.model.interfaces.IResult;
import lib.ys.util.TextUtil;
import okhttp3.WebSocket;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */
public class LivePresenterImpl1 extends BasePlayPresenterImpl<LiveContract1.View> implements LiveContract1.Presenter {

    private WebSocket mWebSocket;

    public LivePresenterImpl1(LiveContract1.View view) {
        super(view);
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
        mWebSocket = exeWebSocketReq(NetFactory.webLive(url), new WebSocketImpl());
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
                // 直播
                List<Course> courses = getCourses();
                if (courses == null || index > courses.size() - 1) {
                    return;
                }
                Course c = courses.get(index);
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

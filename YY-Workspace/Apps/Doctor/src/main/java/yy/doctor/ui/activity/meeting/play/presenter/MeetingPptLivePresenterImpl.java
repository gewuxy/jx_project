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
import yy.doctor.ui.activity.meeting.play.contract.MeetingPptLiveContract;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */

public class MeetingPptLivePresenterImpl extends MeetingPptPresenterImpl implements MeetingPptLiveContract.Presenter {

    private final int KCloseNormal = 1000; //  1000表示正常关闭,意思是建议的连接已经完成了
    private WebSocket mWebSocket;

    public MeetingPptLivePresenterImpl(MeetingPptLiveContract.View view) {
        super(view);
    }

    @Override
    public MeetingPptLiveContract.View getView() {
        return (MeetingPptLiveContract.View) super.getView();
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
    public void onDestroy() {
        super.onDestroy();

        if (mWebSocket != null) {
            mWebSocket.close(KCloseNormal, "close");
        }
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

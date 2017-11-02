package yy.doctor.ui.activity.meeting.play.presenter;

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
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

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
        public void onMessage(int order, Course course) {
            if (order == 0) {
                // 直播
                Course c = mCourses.get(mCourses.size() - 1);
                c.put(TCourse.audioUrl, course.getString(TCourse.audioUrl));
            } else {
                // 同步
                /*course.put(TCourse.videoUrl, o.optString(KVideoUrl));
                course.put(TCourse.imgUrl, o.optString(KImgUrl));
                course.put(TCourse.id, o.optString(KId));

                UtilEx.runOnUIThread(() -> getView().addCourse(course));*/

            }
        }

        @Override
        public void reconnect() {

        }

       /* private final String KOrder = "order";
        private final String KAudioUrl = "audioUrl";
        private final String KVideoUrl = "videoUrl";
        private final String KImgUrl = "imgUrl";
        private final String KId = "id";

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            YSLog.d(TAG, "onOpen:" + response.message());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            YSLog.d(TAG, "onMessage:String" + text);

            try {
                JSONObject o = new JSONObject(text);
                int orderId = o.optInt(KOrder);
                if (orderId == 0) {
                    // 直播
                    Course course = mCourses.get(mCourses.size() - 1);
                    course.put(TCourse.audioUrl, o.optString(KAudioUrl));
                } else {
                    // 同步
                    Course course = new Course();
                    course.put(TCourse.videoUrl, o.optString(KVideoUrl));
                    course.put(TCourse.imgUrl, o.optString(KImgUrl));
                    course.put(TCourse.id, o.optString(KId));

                    UtilEx.runOnUIThread(() -> getView().addCourse(course));

                }
            } catch (JSONException e) {
                YSLog.d(TAG, "onMessage:" + e.getMessage());
            }
            // FIXME:先不考虑2
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            YSLog.d(TAG, "onFailure:");
            // 2S秒后重连
            UtilEx.runOnUIThread(() -> {
                if (Util.noNetwork()) {
                    return;
                }

                // 重连
                if (mPpt != null) {
                    String url = mPpt.getString(TPPT.socketUrl);
                    mWebSocket = exeWebSocketReq(NetFactory.webLive(url), new WebSocketImpl());
                }
            }, TimeUnit.SECONDS.toMillis(2));
        }*/

    }
}

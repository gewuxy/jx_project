package yy.doctor.ui.activity.meeting.play;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import lib.ys.YSLog;
import lib.ys.util.UtilEx;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.TCourse;
import yy.doctor.util.Util;


abstract public class MeetWebSocketListener extends WebSocketListener {

    private final String TAG = getClass().getSimpleName();

    private final String KOrder = "order";
    private final String KAudioUrl = "audioUrl";
    private final String KVideoUrl = "videoUrl";
    private final String KImgUrl = "imgUrl";
    private final String KId = "id";

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        YSLog.d(TAG, "onMessage:String" + text);

        try {
            JSONObject o = new JSONObject(text);
            int order = o.optInt(KOrder);
            Course course = new Course();
            course.put(TCourse.videoUrl, o.optString(KVideoUrl));
            course.put(TCourse.imgUrl, o.optString(KImgUrl));
            course.put(TCourse.id, o.optString(KId));
            course.put(TCourse.audioUrl, o.optString(KAudioUrl));
            onMessage(order, course);
        } catch (JSONException e) {
            YSLog.d(TAG, "onMessage:" + e.getMessage());
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        YSLog.d(TAG, "onFailure:");
        UtilEx.runOnUIThread(() -> {
            if (Util.noNetwork()) {
                return;
            }

            // 重连
            reconnect();
        }, TimeUnit.SECONDS.toMillis(2));
    }

    abstract public void onMessage(int order, Course course);

    /**
     * 2S秒后重连
     */
    abstract public void reconnect();

}
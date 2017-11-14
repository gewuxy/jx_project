package yy.doctor.ui.activity.meeting.play;

import android.support.annotation.IntDef;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import lib.ys.YSLog;
import lib.ys.util.UtilEx;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.TCourse;
import yy.doctor.util.Util;

/**
 * 观看会议用的(不包括评论)
 */
abstract public class MeetWebSocketListener extends WebSocketListener {

    private final String TAG = getClass().getSimpleName();

    private static final int KCloseNormal = 1000; //  1000表示正常关闭,意思是建议的连接已经完成了

    private final String KOrder = "order";
    private final String KAudioUrl = "audioUrl";
    private final String KVideoUrl = "videoUrl";
    private final String KImgUrl = "imgUrl";
    private final String KId = "id";
    private final String KIndex = "pageNum";
    private final String KOnline = "onLines";

    @IntDef({
            OrderType.live,
            OrderType.synchronize,
            OrderType.online,
    })
    @Retention(RetentionPolicy.SOURCE)
    protected @interface OrderType {
        int live = 0; // 直播
        int synchronize = 1; // 同步
        int online = 6; // 观众人数
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        YSLog.d(TAG, "onMessage:Open");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        YSLog.d(TAG, "onMessage:String" + text);

        try {
            JSONObject o = new JSONObject(text);
            int order = o.optInt(KOrder);
            if (order == OrderType.synchronize || order == OrderType.live) {
                int index = o.optInt(KIndex);
                Course course = new Course();
                course.put(TCourse.id, o.optString(KId));
                course.put(TCourse.imgUrl, o.optString(KImgUrl));
                course.put(TCourse.videoUrl, o.optString(KVideoUrl));
                course.put(TCourse.audioUrl, o.optString(KAudioUrl));
                UtilEx.runOnUIThread(() -> onMessage(order, index, course));
            } else if (order == OrderType.online) {
                UtilEx.runOnUIThread(() -> online(o.optInt(KOnline)));
            }
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

    public static void close(WebSocket socket) {
        if (socket == null) {
            return;
        }
        boolean close = socket.close(KCloseNormal, "close");
        YSLog.d(MeetWebSocketListener.class.getName(), "close:" + close);
    }

    abstract protected void online(int onlineNum);

    abstract protected void onMessage(int order, int index, Course course);

    /**
     * 2S秒后重连
     */
    abstract protected void reconnect();

}
package lib.jx.notify;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.NotifierEx;
import lib.jx.notify.LiveNotifier.OnLiveNotify;

/**
 * 直播的通知
 *
 * @author caixiang
 */

public class LiveNotifier extends NotifierEx<OnLiveNotify> {

    @IntDef({
            LiveNotifyType.sync,
            LiveNotifyType.reject,
            LiveNotifyType.accept,
            LiveNotifyType.inquired,
            LiveNotifyType.send_msg,
            LiveNotifyType.online_num,
            LiveNotifyType.flow_insufficient,
            LiveNotifyType.flow_run_out_of,
            LiveNotifyType.flow_sufficient
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface LiveNotifyType {
        int sync = 1;  // 收到web端同步指令  直播、录播页面接收
        int reject = 2;   // 收到拒绝被踢指令  首页、扫描页接收
        int accept = 3;   // 收到同意被踢指令  首页、扫描页接收
        int inquired = 4;  // 当前客户端收到被被踢的询问   直播、录播页面接收
        int send_msg = 5;  // 客户端发送websocket的指令
        int online_num = 6;  // 直播间在线人数 用于语音直播间
        int flow_insufficient = 7; // 流量不足预警  语音直播页面中接收 视频直播中接收到显示红色警告
        int flow_run_out_of = 8; // 流量耗尽通知  视频直播中接收 收到直接关闭视频
        int flow_sufficient = 9; // 流量足够通知  视频直播接收 去除红色警告
    }

    @Override
    protected void callback(OnLiveNotify o, int type, Object data) {
        o.onLiveNotify(type, data);
    }

    public interface OnLiveNotify {
        void onLiveNotify(@LiveNotifyType int type, Object data);
    }

    private static LiveNotifier mInst = null;

    public static LiveNotifier inst() {
        if (mInst == null) {
            synchronized (LiveNotifier.class) {
                if (mInst == null) {
                    mInst = new LiveNotifier();
                }
            }
        }
        return mInst;
    }

}

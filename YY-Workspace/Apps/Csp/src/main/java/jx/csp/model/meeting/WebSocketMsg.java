package jx.csp.model.meeting;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.model.meeting.WebSocketMsg.TWebSocketMsg;
import lib.ys.model.EVal;

/**
 * WebSocket信息
 *
 * @author CaiXiang
 * @since 2017/10/20
 */

public class WebSocketMsg extends EVal<TWebSocketMsg> {

    @StringDef({
            WsOrderFrom.app,
            WsOrderFrom.web
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WsOrderFrom {
        String app = "app";
        String web = "web";
    }

    @IntDef({
            WsOrderType.live,
            WsOrderType.sync,
            WsOrderType.inquire,
            WsOrderType.reject,
            WsOrderType.accept,
            WsOrderType.repeat_login,
            WsOrderType.onlineNum,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WsOrderType {
        int live = 0;
        int sync = 1;
        int inquire = 2;
        int reject = 3;
        int accept = 4;
        int repeat_login = 5;
        int onlineNum = 6;
    }

    public enum TWebSocketMsg {
        courseId,  // 课程id
        /**
         * {@link WsOrderType}
         */
        order,    //  0表示直播指令 1同步指令 2表示踢人指令 3表示拒绝被 4接受被踢 5存在重复登录 6直播间人数
        orderFrom,  // app  web
        pageNum,  // 页面下标 0开始
        sid, // 会话ID
        imgUrl, // 图片地址
        videoUrl, // 视频地址
        detailId, // 资料id
        onLines, // 在线人数
    }
}

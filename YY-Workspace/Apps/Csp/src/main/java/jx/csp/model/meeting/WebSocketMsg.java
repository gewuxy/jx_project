package jx.csp.model.meeting;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.model.meeting.WebSocketMsg.TWebSocketMsg;
import lib.ys.model.EVal;

/**
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
            WsOrderType.sync
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WsOrderType {
        int sync = 1;
    }

    public enum TWebSocketMsg {
        courseId,  // 课程id
        order,    // 1 同步指令
        orderFrom,  // app  web
        pageNum,  // 页面下标 0开始
    }
}

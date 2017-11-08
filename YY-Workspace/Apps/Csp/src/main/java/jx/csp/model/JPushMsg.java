package jx.csp.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.model.JPushMsg.TJPushMsg;
import lib.ys.model.EVal;

/**
 * @author Huoxuyu
 * @since 2017/10/20
 */

public class JPushMsg extends EVal<TJPushMsg> {

    @IntDef({
            MsgType.common,
            MsgType.meeting,
            MsgType.bind_email_success,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MsgType {
        int common = 0; // 普通消息  不能跳转
        int meeting = 1; // 会议消息
        int bind_email_success = 3; // 绑定邮箱成功
    }

    public enum TJPushMsg {
        content,
        msgType,
        title,
        result,
    }

}

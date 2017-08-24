package yy.doctor.model.jpush;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import yy.doctor.model.jpush.JPushMsg.TJPushMsg;

/**
 * @author CaiXiang
 * @since 2017/6/10
 */

public class JPushMsg extends EVal<TJPushMsg> {

    @IntDef({
            MsgType.common,
            MsgType.meeting,
            MsgType.epn_change,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MsgType {
        int common = 0; // 普通消息  不能跳转
        int meeting = 1; // 会议消息
        int epn_change = 2; // 象数改变消息
    }

    public enum TJPushMsg {
        content,
        msgType,
        meetId,
        meetName,
        sendTime,
        senderName,
        title,
        result,
    }

}

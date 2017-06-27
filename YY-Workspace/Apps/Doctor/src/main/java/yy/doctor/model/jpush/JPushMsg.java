package yy.doctor.model.jpush;

import lib.ys.model.EVal;
import yy.doctor.model.jpush.JPushMsg.TJPushMsg;

/**
 * @author CaiXiang
 * @since 2017/6/10
 */

public class JPushMsg extends EVal<TJPushMsg> {

    public enum TJPushMsg {
        content,
        msgType,
        meetId,
        meetName,
        sendTime,
        senderName,
        title,

        groupId,
        receiver,
        sender,
        state
    }

}

package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.Histories.THistories;

/**
 * 会议留言记录
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Histories extends EVal<THistories> {
    public enum THistories {
        id,//留言id
        meetId,//会议ID
        message,//留言内容
        msgType,//留言类型
        sender,//发送者姓名
        senderId,//发送者ID
    }
}

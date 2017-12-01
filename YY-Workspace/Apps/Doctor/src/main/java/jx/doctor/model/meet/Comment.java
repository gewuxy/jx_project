package jx.doctor.model.meet;

import lib.ys.model.EVal;
import jx.doctor.model.meet.Comment.TComment;

/**
 * 会议留言记录
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Comment extends EVal<TComment> {
    public enum TComment {
        id, // 留言id
        meetId, // 会议ID
        message, // 留言内容
        msgType, // 留言类型
        sender, // 发送者姓名
        senderId, // 发送者ID
        sendTime, // 发送时间
        headimg, // 头像url
    }
}

package jx.doctor.model.me;

import lib.ys.model.EVal;
import jx.doctor.model.me.CollectionMeetings.TCollectionMeetings;

/**
 * @author CaiXiang
 * @since 2017/5/26
 */

public class CollectionMeetings extends EVal<TCollectionMeetings> {

    public enum TCollectionMeetings {
        id, // 会议id
        meetName,    // 会议名称
        meetType,    // 会议科室类型
        startTime,    // 会议开始时间
        endTime,    // 会议结束时间
        state,    // 会议状态
        ownerId,    // 会议发布者id
        nickname,    // 会议发布者昵称
        headimg,    // 会议发布者头像
    }

}

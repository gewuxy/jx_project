package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.search.IRec;

/**
 * 关注的会议
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class Meeting extends EVal<TMeeting> implements IRec {
    @Override
    public int getType() {
        return RecType.meeting;
    }

    public enum TMeeting {
        id,             // 会议ID
        meetName,       // 会议名称
        meetType,       // 会议科室类型
        organizer,      // 会议主办方
        state,          // 会议状态
        headimg, // 头像Url
        startTime, // 开始时间
        endTime, // 结束时间
    }
}

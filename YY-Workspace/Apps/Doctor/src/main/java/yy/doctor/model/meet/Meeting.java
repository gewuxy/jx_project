package yy.doctor.model.meet;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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

    @IntDef({
            FileType.folder,
            FileType.file,
    })
    public  @interface FileType {
        int folder = 0;
        int file = 1;
    }

    /**
     * 会议状态
     */
    @IntDef({
            MeetState.not_started,
            MeetState.under_way,
            MeetState.retrospect,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MeetState {
        int not_started = 1; // 未开始
        int under_way = 2; // 进行中
        int retrospect = 3; // 精彩回顾
    }

    @Override
    public int getType() {
        return RecType.meeting;
    }

    public enum TMeeting {
        id,             // 会议ID
        meetName,       // 会议名称
        meetType,       // 会议科室类型
        organizer,      // 会议主办方

        /**
         * {@link MeetState}
         */
        state,          // 会议状态

        headimg, // 头像Url
        startTime, // 开始时间
        endTime, // 结束时间
    }

}

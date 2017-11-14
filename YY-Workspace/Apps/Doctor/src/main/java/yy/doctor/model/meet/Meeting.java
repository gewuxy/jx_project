package yy.doctor.model.meet;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import yy.doctor.model.home.Lecturer;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.search.IRec;

/**
 * 关注的会议
 *
 * @author GuoXuan
 * @since 2017/5/5
 */
public class Meeting extends EVal<TMeeting> implements IRec {

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

    /**
     * 会议直播状态
     */
    @IntDef({
            LiveState.not_started,
            LiveState.under_way,
            LiveState.end,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface LiveState {
        int not_started = 1; // 未开始
        int under_way = 2; // 进行中
        int end = 3; // 已结束
    }

    /**
     * 会议类型
     */
    @IntDef({
            MeetType.folder,
            MeetType.meet,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MeetType {
        int folder = 0; // 文件夹
        int meet = 1; // 文件
    }

    @Override
    public int getRecType() {
        return getInt(TMeeting.type) == MeetType.meet ? RecType.meeting : RecType.meet_folder;
    }

    public enum TMeeting {

        /**
         * {@link MeetState}
         */
        state, //  3, 会议状态

        /**
         * {@link MeetType}
         */
        @Init(asInt = MeetType.meet)
        type, //  1, 文件

        id, //  17062316384929986180,会议ID
        startTime, //  1501752518401,开始时间
        endTime, //  1501752518401,结束时间

        meetName, //  2015年莆田市基层医疗卫生机构考试（下）,会议名称
        meetCount, //  0, 会议数量
        meetType, //  其他科室, 会议科室类型
        organizer, //  莆田卫生培训, 会议主办方

        requiredXs, // flase, 不奖励(支付)象数
        xsCredits, // 2, 象数

        rewardCredit, //  flase, 不奖励学分
        eduCredits, //   0, 学分

        @Bind(asList = Lecturer.class)
        lecturerList,

        lecturer, //  , 名字
        lecturerDepart, //  ,  科室
        lecturerHead, //  http://10.0.0.252/others/metting-img-man.jpg,  头像
        lecturerHos, //  ,  医院
        lecturerTitle, //  ,  职称

        completeProgress, // 学习进度

        /**
         * {@link LiveState}
         */
        liveState, // 直播状态

        userId, //  287564, 无用
    }

}

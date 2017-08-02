package yy.doctor.model.home;

import lib.ys.model.EVal;
import yy.doctor.model.home.RecMeeting.TRecMeeting;
import yy.doctor.model.meet.Meeting.MeetState;

/**
 * Created by XPS on 2017/5/12.
 */

public class RecMeeting extends EVal<TRecMeeting> implements IHome {

    @Override
    public int getType() {
        return HomeType.meeting;
    }

    public enum TRecMeeting {

        id,    //会议ID
        lecturer,    //主讲者
        lecturerTile,    //主讲者职位
        meetName,    //会议名称
        meetType,    //会议科室类型
        endTime,
        startTime,
        pubUserHead, // 单位号头像
        pubUserId, // 单位号id
        lecturerImg,  //主讲者头像
        lecturerHos, //主讲者医院
        stored, // 是否已经收藏， 0 未收藏  1 收藏
        requiredXs, // flase 不奖励(支付)象数
        xsCredits, // 0  象数
        rewardCredit, //  flase 不奖励学分

        /**
         * {@link MeetState}
         */
        state,
    }
}

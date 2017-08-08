package yy.doctor.ui.frag.meeting;

import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.network.NetFactory;

/**
 * 会议等待中列表
 * <p>
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class NotStartedMeetingsFrag extends BaseMeetingsFrag {

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.meets(MeetState.not_started, mDepart, getOffset(), getLimit()));
    }
}

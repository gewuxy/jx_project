package yy.doctor.ui.frag.meeting;

import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.network.NetFactory;

/**
 * 会议进行中列表
 *
 * @auther yuansui
 * @since 2017/4/24
 */

public class UnderWayMeetingsFrag extends BaseMeetingsFrag {

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.meets(MeetState.under_way, mDepart, getOffset(), getLimit()));
    }
}

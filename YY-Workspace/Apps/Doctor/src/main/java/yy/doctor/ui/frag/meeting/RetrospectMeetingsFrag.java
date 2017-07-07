package yy.doctor.ui.frag.meeting;

import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.network.NetFactory;

/**
 * 会议精彩回顾列表
 * <p>
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class RetrospectMeetingsFrag extends BaseMeetingsFrag {

    @Override
    public void initData() {
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.meets(MeetState.retrospect, mDepart, getOffset(), getLimit()));
    }
}

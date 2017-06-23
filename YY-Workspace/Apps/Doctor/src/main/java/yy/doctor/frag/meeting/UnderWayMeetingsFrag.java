package yy.doctor.frag.meeting;

import yy.doctor.Constants.MeetsState;
import yy.doctor.Extra;
import yy.doctor.network.NetFactory;

/**
 * 会议进行中列表
 *
 * @auther yuansui
 * @since 2017/4/24
 */

public class UnderWayMeetingsFrag extends BaseMeetingsFrag {

    @Override
    public void initData() {
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.meets(MeetsState.under_way, mDepart, getOffset(), getLimit()));
    }

    @Override
    public int getLimit() {
        return Extra.KPageSize;
    }
}

package yy.doctor.frag.meeting;

import lib.ys.config.AppConfig.RefreshWay;
import yy.doctor.Constants.MeetsState;
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

    }

    @Override
    public void setViews() {
        super.setViews();
        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.meets(MeetsState.under_way));
    }

}

package yy.doctor.frag.meeting;

import lib.ys.config.AppConfig.RefreshWay;
import yy.doctor.Constants.MeetsState;
import yy.doctor.network.NetFactory;

/**
 * 会议等待中列表
 *
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class NotStartedMeetingsFrag extends BaseMeetingsFrag {
    @Override
    public void initData() {

    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(0, NetFactory.meets(MeetsState.not_started));
    }

    @Override
    public void setViews() {
        super.setViews();
        refresh(RefreshWay.embed);
        exeNetworkReq(0, NetFactory.meets(MeetsState.not_started));
    }

}

package yy.doctor.activity.me.epn;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.EpnDetailsAdapter;
import yy.doctor.model.me.EpnDetails;
import yy.doctor.network.NetFactory;

/**
 * 象数明细
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpnDetailsActivity extends BaseSRListActivity<EpnDetails, EpnDetailsAdapter> {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "明细", this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.epnDetails());
    }

}

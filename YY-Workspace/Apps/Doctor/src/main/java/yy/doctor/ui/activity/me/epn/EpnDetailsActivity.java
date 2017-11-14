package yy.doctor.ui.activity.me.epn;

import android.os.Bundle;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.me.EpnDetailsAdapter;
import yy.doctor.model.me.EpnDetails;
import yy.doctor.network.NetworkApiDescriptor.EpcAPI;
import yy.doctor.util.Util;

/**
 * 象数明细
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpnDetailsActivity extends BaseSRListActivity<EpnDetails, EpnDetailsAdapter> {

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.epn_detail, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(EpcAPI.epnDetails().build());
    }

}

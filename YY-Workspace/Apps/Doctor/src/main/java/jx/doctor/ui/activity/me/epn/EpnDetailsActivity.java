package jx.doctor.ui.activity.me.epn;

import android.os.Bundle;

import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseSRListActivity;
import jx.doctor.R;
import jx.doctor.adapter.me.EpnDetailsAdapter;
import jx.doctor.model.me.EpnDetails;
import jx.doctor.network.NetworkApiDescriptor.EpcAPI;
import jx.doctor.util.Util;

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
        Util.addBackIcon(bar, R.string.epn_detail, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(EpcAPI.epnDetails().build());
    }

}

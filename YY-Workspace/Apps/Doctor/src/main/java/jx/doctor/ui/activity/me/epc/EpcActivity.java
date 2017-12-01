package jx.doctor.ui.activity.me.epc;

import android.os.Bundle;

import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseSRListActivity;
import jx.doctor.R;
import jx.doctor.adapter.me.EpcAdapter;
import jx.doctor.model.me.Epc;
import jx.doctor.network.NetworkApiDescriptor.EpcAPI;
import jx.doctor.util.Util;

/**
 * 象城
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcActivity extends BaseSRListActivity<Epc, EpcAdapter> {

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.epc, this);
        bar.addTextViewRight(R.string.order, v -> startActivity(OrderActivity.class));
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(EpcAPI.epc(getOffset(), getLimit()).build());
    }
}

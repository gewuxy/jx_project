package jx.csp.ui.activity.me;

import android.os.Bundle;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.adapter.me.HistoryDetailAdapter;
import jx.csp.model.me.HistoryDetail;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseSRListActivity;

/**
 * 投稿历史详情
 *
 * @auther WangLan
 * @since 2017/10/16
 */
@Route
public class ContributeHistoryDetailActivity extends BaseSRListActivity<HistoryDetail, HistoryDetailAdapter> {

    @Arg
    public int mAcceptId;

    @Arg
    public String mTitle;

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mTitle, this);
    }

    @Override
    public void setViews() {
        super.setViews();
        setDividerHeight(0);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(DeliveryAPI.historyDetail(mAcceptId).build());
    }
}

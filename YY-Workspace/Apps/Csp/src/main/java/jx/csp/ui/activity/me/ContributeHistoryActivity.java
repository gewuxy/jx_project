package jx.csp.ui.activity.me;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.me.HistoryAdapter;
import jx.csp.model.me.History;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseSRListActivity;

/**
 * @auther WangLan
 * @since 2017/10/13
 */

public class ContributeHistoryActivity extends BaseSRListActivity<History, HistoryAdapter> {

    private final int KLimit = 16; // 每页展示的数据

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getString(R.string.contribute_history), this);
    }

    @Override
    public void setViews() {
        super.setViews();
        setDividerHeight(0);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(DeliveryAPI.history("99efb894ecac41ccab822811ffc79276").pageNum(getOffset()).pageSize(getLimit()).build());
    }

    @Override
    public int getLimit() {
        return KLimit;
    }

    @Override
    public int getOffset() {
        return 1;
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_empty_history_footer);
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.contribute_not_submit_records_footer);
    }

}

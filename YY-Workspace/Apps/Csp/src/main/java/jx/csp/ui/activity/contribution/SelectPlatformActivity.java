package jx.csp.ui.activity.contribution;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.contribution.SelectPlatformAdapter;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseListActivity;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;

/**
 * 投稿  选择平台
 *
 * @author CaiXiang
 * @since 2018/3/9
 */

public class SelectPlatformActivity extends BaseListActivity<UnitNum, SelectPlatformAdapter> implements OnAdapterClickListener {

    @Override
    public void initData() {

        for (int i = 1; i < 6; ++i) {
            UnitNum unitNum = new UnitNum();
            unitNum.put(TUnitNum.name, "平台 " + i);
            addItem(unitNum);
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.select_platform, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(0);
        setOnAdapterClickListener(this);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        if (v.getId() == R.id.select_platform_item_tv_contribution) {
            showToast("hello");
        }
    }
}

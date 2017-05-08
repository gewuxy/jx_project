package yy.doctor.activity.me;

import android.view.View;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.adapter.UnitNumDataAdapter;
import yy.doctor.util.Util;

/**
 * 单位号资料
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class UnitNumDataActivity extends BaseListActivity<String> {

    @Override
    public void initData() {

        for (int i = 0; i < 12; ++i) {
            addItem(i + "");
        }

    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "资料", this);

    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new UnitNumDataAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);

        startActivity(UnitNumDataDetailActivity.class);
    }
}

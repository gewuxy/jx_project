package yy.doctor.activity.me;

import android.view.View;

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
public class UnitNumDataActivity extends BaseListActivity<String, UnitNumDataAdapter> {

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
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);

        startActivity(DownloadDataActivity.class);
    }
}

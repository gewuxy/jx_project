package yy.doctor.activity.me;

import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 单位号资料详情
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class UnitNumDataDetailActivity extends BaseActivity {

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_unit_num_data_detail;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "sdfs", this);

    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {

    }

}

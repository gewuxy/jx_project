package jx.doctor.ui.activity.me;

import android.os.Bundle;
import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseActivity;
import jx.doctor.R;
import jx.doctor.util.Util;

/**
 * 参会统计
 *
 * @auther : GuoXuan
 * @since : 2017/7/25
 */
public class StatsMeetActivity extends BaseActivity {

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_statistics_meet;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "参会统计", this);
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
    }

}

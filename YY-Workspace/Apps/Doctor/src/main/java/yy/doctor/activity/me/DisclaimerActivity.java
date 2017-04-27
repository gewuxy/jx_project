package yy.doctor.activity.me;

import android.support.annotation.NonNull;

import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 免责声明
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class DisclaimerActivity extends BaseActivity {

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_disclaimer;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "免责声明", this);
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {

    }

}

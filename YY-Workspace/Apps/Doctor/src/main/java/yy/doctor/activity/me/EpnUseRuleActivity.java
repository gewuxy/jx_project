package yy.doctor.activity.me;

import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 象数使用规则
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class EpnUseRuleActivity extends BaseActivity {

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_epn_use_rule;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "象数使用规则", this);
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {

    }

}

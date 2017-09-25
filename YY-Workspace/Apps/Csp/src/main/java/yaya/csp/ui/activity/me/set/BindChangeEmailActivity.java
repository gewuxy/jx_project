package yaya.csp.ui.activity.me.set;

import android.support.annotation.NonNull;
import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yaya.csp.R;
import yaya.csp.util.Util;

/**
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class BindChangeEmailActivity extends BaseActivity{


    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_bind_change_email;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.setting_bind_email, this);
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.set_tv_change_email);
    }

    @Override
    public void onClick(View v) {
        startActivity(BindEmailActivity.class);
    }
}

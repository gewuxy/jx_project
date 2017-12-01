package jx.csp.ui.activity.me.bind;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.R;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseActivity;

/**
 * 提示绑定邮箱
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class BindEmailTipsActivity extends BaseActivity {

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_bind_email;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.setting_change_pwd, this);
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.set_tv_bind_email);
    }

    @Override
    public void onClick(View v) {
        startActivity(BindEmailActivity.class);
        finish();
    }
}

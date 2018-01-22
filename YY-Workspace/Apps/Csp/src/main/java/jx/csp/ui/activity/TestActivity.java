package jx.csp.ui.activity;

import android.content.Intent;

import jx.csp.ui.activity.login.auth.AuthLoginActivity;
import jx.csp.ui.activity.main.MainActivity;
import lib.jx.test.BaseTestActivity;

/**
 * @auther yuansui
 * @since 2017/9/20
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {
        add("首页", new Intent(this, MainActivity.class));
        add("登录", new Intent(this, AuthLoginActivity.class));
    }

}

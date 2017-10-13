package jx.csp.ui.activity;

import android.content.Intent;

import jx.csp.ui.activity.login.EmailLoginActivity;
import jx.csp.ui.activity.login.ThirdPartyLoginActivity;
import jx.csp.ui.activity.me.MeActivity;
import lib.yy.test.BaseTestActivity;

/**
 * @auther yuansui
 * @since 2017/9/20
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {

        add("个人中心", new Intent(this, MeActivity.class));
        add("邮箱登录", new Intent(this, EmailLoginActivity.class));
        add("登录", new Intent(this, ThirdPartyLoginActivity.class));
    }
}
package jx.csp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import jx.csp.ui.activity.login.AuthLoginActivity;
import jx.csp.ui.activity.login.CaptchaLoginActivity;
import jx.csp.ui.activity.login.EmailLoginActivity;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.ui.activity.me.MeActivity;
import lib.yy.test.BaseTestActivity;

/**
 * @auther yuansui
 * @since 2017/9/20
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData(Bundle savedInstanceState) {

        add("首页", new Intent(this, MainActivity.class));
        add("个人中心", new Intent(this, MeActivity.class));
        add("邮箱登录", new Intent(this, EmailLoginActivity.class));
        add("登录", new Intent(this, AuthLoginActivity.class));
        add("手机登录", new Intent(this, CaptchaLoginActivity.class));
    }
}

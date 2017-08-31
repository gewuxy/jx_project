package yy.doctor.ui.activity;

import android.content.Intent;

import lib.yy.test.BaseTestActivity;
import yy.doctor.ui.activity.user.login.LoginActivity;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {

        add("主页", MainActivityRouter.newIntent(this, MainActivity.KTabHome));
        add("会议", MainActivityRouter.newIntent(this, MainActivity.KTabMeeting));
        add("数据", MainActivityRouter.newIntent(this, MainActivity.KTabData));
        add("我", MainActivityRouter.newIntent(this, MainActivity.KTabMe));

        add("登陆", new Intent(this, LoginActivity.class));
    }

}

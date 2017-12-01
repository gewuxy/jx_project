package jx.doctor.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import jx.doctor.ui.activity.user.login.LoginActivity;
import lib.jx.test.BaseTestActivity;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData(Bundle state) {

        add("主页", MainActivityRouter.newIntent(this, MainActivity.KTabHome));
        add("会议", MainActivityRouter.newIntent(this, MainActivity.KTabMeeting));
        add("数据", MainActivityRouter.newIntent(this, MainActivity.KTabData));
        add("我", MainActivityRouter.newIntent(this, MainActivity.KTabMe));

        add("", view -> {});
        add("登陆", new Intent(this, LoginActivity.class));
        add("", view -> {});
        add("广告", AdActivity.class);
    }

}

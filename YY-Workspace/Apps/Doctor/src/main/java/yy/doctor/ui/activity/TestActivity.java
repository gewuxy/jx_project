package yy.doctor.ui.activity;

import lib.yy.test.BaseTestActivity;
import yy.doctor.ui.activity.login.LoginActivity;
import yy.doctor.ui.activity.me.set.BindEmailActivity;
import yy.doctor.ui.activity.me.set.BindPhoneActivity;
import yy.doctor.ui.activity.me.set.ChangePwdActivity;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {

        add("主页", MainActivity.newIntent(this, MainActivity.KTabHome));
        add("会议", MainActivity.newIntent(this, MainActivity.KTabMeeting));
        add("数据", MainActivity.newIntent(this, MainActivity.KTabData));
        add("我", MainActivity.newIntent(this, MainActivity.KTabMe));

        add("登录", LoginActivity.class);
        add("绑定邮箱", BindEmailActivity.class);
        add("绑定手机", BindPhoneActivity.class);
        add("更换密码", ChangePwdActivity.class);
    }

}

package yy.doctor.ui.activity;

import lib.yy.test.BaseTestActivity;
import yy.doctor.ui.activity.login.LoginActivity;
import yy.doctor.ui.activity.me.profile.SectionActivity;
import yy.doctor.ui.activity.me.profile.TitleActivity;
import yy.doctor.ui.activity.register.RegisterActivity;
import yy.doctor.ui.activity.search.SearchHospitalActivity;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {

        add("主页", MainActivityIntent.newIntent(this, MainActivity.KTabHome));
        add("会议", MainActivityIntent.newIntent(this, MainActivity.KTabMeeting));
        add("数据", MainActivityIntent.newIntent(this, MainActivity.KTabData));
        add("我", MainActivityIntent.newIntent(this, MainActivity.KTabMe));

        add("登录", LoginActivity.class);
        add("注册", RegisterActivity.class);
        add("扫一扫", ScanActivity.class);
        add("手电筒", FlashActivity.class);
        add("搜索医院", SearchHospitalActivity.class);
        add("职称", TitleActivity.class);
        add("专科", SectionActivity.class);

    }

}

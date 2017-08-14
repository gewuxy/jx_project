package yy.doctor.ui.activity;

import lib.yy.test.BaseTestActivity;
import yy.doctor.serv.GlConfigServIntent;
import yy.doctor.ui.activity.me.set.BindEmailActivity;

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


        add("邮箱", BindEmailActivity.class);
        add("123", v -> {
            GlConfigServIntent.create()
                    .start(this);
        });
    }

    private void download() {

    }

}

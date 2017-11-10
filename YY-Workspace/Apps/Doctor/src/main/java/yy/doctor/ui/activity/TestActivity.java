package yy.doctor.ui.activity;

import android.content.Intent;

import lib.yy.test.BaseTestActivity;
import yy.doctor.ui.activity.meeting.play.MeetingLiveActivity;
import yy.doctor.ui.activity.meeting.play.MeetingLiveActivityRouter;
import yy.doctor.ui.activity.meeting.play.MeetingPptLiveActivityRouter;
import yy.doctor.ui.activity.meeting.play.MeetingRebActivityRouter;
import yy.doctor.ui.activity.user.login.LoginActivity;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData(Bundle savedInstanceState) {

        add("主页", MainActivityRouter.newIntent(this, MainActivity.KTabHome));
        add("会议", MainActivityRouter.newIntent(this, MainActivity.KTabMeeting));
        add("数据", MainActivityRouter.newIntent(this, MainActivity.KTabData));
        add("我", MainActivityRouter.newIntent(this, MainActivity.KTabMe));

        add("", view -> {});
        add("登陆", new Intent(this, LoginActivity.class));
        add("", view -> {});
        add("录播会议", v -> MeetingRebActivityRouter.create("17060312172856248750", "33").route(this));
        add("直播会议", v -> MeetingLiveActivityRouter.create("17060312172856248750", "33").route(this));
        add("ppt直播", v -> MeetingPptLiveActivityRouter.create("17060312172856248750", "33").route(this));
        add("", view -> {});
        add("广告", AdActivity.class);
    }

}

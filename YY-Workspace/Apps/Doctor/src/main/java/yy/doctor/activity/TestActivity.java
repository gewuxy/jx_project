package yy.doctor.activity;

import lib.yy.test.BaseTestActivity;
import yy.doctor.activity.me.ChangePwdActivity;
import yy.doctor.activity.me.DownloadDataActivity;
import yy.doctor.activity.me.EpcActivity;
import yy.doctor.activity.me.EpcDetailActivity;
import yy.doctor.activity.me.EpnActivity;
import yy.doctor.activity.me.EpnUseRuleActivity;
import yy.doctor.activity.me.ExchangeActivity;
import yy.doctor.activity.me.ProfileActivity;
import yy.doctor.activity.me.TitleActivity;
import yy.doctor.activity.me.UnitNumActivity;
import yy.doctor.activity.me.UnitNumDetailActivity;
import yy.doctor.activity.meeting.ExamIntroActivity;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.activity.meeting.VideoActivity;
import yy.doctor.activity.register.ProvinceActivity;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.dialog.UpdateNoticeDialog;

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

        add("个人资料", ProfileActivity.class);
        add("开始考试", v -> ExamIntroActivity.nav(TestActivity.this,"17042512131640894904","8"));
        add("视频", VideoActivity.class);
        add("会议详情", MeetingDetailsActivity.class);

        add("登录", LoginActivity.class);
        add("修改密码", ChangePwdActivity.class);
        add("忘记密码", ForgetPwdActivity.class);

        add("象数使用规则", EpnUseRuleActivity.class);
        add("我的象数", EpnActivity.class);

        add("更新对话框", new UpdateNoticeDialog(this));
        add("分享对话框", new ShareDialog(this));

        add("单位号详情", UnitNumDetailActivity.class);
        add("象城", EpcActivity.class);
        add("兑换", ExchangeActivity.class);
        add("商品详情", EpcDetailActivity.class);
        add("单位号", UnitNumActivity.class);
        add("省", ProvinceActivity.class);
        add("职称", TitleActivity.class);
        add("资料下载", DownloadDataActivity.class);
    }

}

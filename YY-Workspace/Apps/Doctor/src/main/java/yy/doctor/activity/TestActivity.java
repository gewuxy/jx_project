package yy.doctor.activity;

import lib.yy.test.BaseTestActivity;
import yy.doctor.activity.me.ChangePwdActivity;
import yy.doctor.activity.me.EpcActivity;
import yy.doctor.activity.me.EpcDetailActivity;
import yy.doctor.activity.me.EpnUseRuleActivity;
import yy.doctor.activity.me.ExchangeActivity;
import yy.doctor.activity.me.ForgetPwdActivity;
import yy.doctor.activity.me.MyEpnActivity;
import yy.doctor.activity.meeting.MeetingRecordActivity;
import yy.doctor.activity.meeting.MeetingSeekActivity;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.dialog.UpdateNoticeDialog;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {
        add("主页", MainActivity.class);

        add("搜索",MeetingSeekActivity.class);
        add("记录",MeetingRecordActivity.class);

        add("登录", LoginActivity.class);
        add("修改密码", ChangePwdActivity.class);
        add("忘记密码", ForgetPwdActivity.class);

        add("象数使用规则", EpnUseRuleActivity.class);
        add("我的象数", MyEpnActivity.class);

        add("更新对话框", new UpdateNoticeDialog(this));
        add("分享对话框", new ShareDialog(this));

        add("单位号", UnitNumDetailActivity.class);
        add("象城", EpcActivity.class);
        add("兑换", ExchangeActivity.class);
        add("商品详情", EpcDetailActivity.class);
    }

}

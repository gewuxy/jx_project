package yy.doctor.activity;

import lib.yy.test.BaseTestActivity;
import yy.doctor.activity.me.ChangePwdActivity;
import yy.doctor.activity.me.EpnUseRuleActivity;
import yy.doctor.activity.me.ForgetPasswordActivity;
import yy.doctor.activity.me.MyEpnActivity;
import yy.doctor.activity.register.HospitalActivity;
import yy.doctor.activity.register.RegisterActivity;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.dialog.UpdateNoticeDialog;
import yy.doctor.frag.MeetingFrag;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {
        add("主页", MainActivity.class);
        add("医院",HospitalActivity.class);
        add("登录", LoginActivity.class);
        add("修改密码", ChangePwdActivity.class);
        add("忘记密码", ForgetPasswordActivity.class);

        add("象数使用规则", EpnUseRuleActivity.class);
        add("我的象数", MyEpnActivity.class);


        add("更新对话框", new UpdateNoticeDialog(this));
        add("分享对话框", new ShareDialog(this));
    }

}

package yy.doctor.activity;

import lib.yy.test.BaseTestActivity;
import yy.doctor.R;
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
import yy.doctor.activity.meeting.ExamTopicActivity;
import yy.doctor.activity.meeting.MeetingCommentActivity;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.activity.meeting.VideoActivity;
import yy.doctor.activity.register.ProvinceActivity;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.dialog.HintDialogSec;

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
        add("开始考试", v -> ExamIntroActivity.nav(TestActivity.this, "17042512131640894904", "8", ""));
        add("视频", VideoActivity.class);
        add("会议详情", MeetingDetailsActivity.class);

        add("登录", LoginActivity.class);
        add("修改密码", ChangePwdActivity.class);
        add("忘记密码", ForgetPwdActivity.class);

        add("象数使用规则", EpnUseRuleActivity.class);
        add("我的象数", EpnActivity.class);

        add("评论", v -> MeetingCommentActivity.nav(this,"17060312172856248750"));

        add("对话框11", v -> {
            HintDialogMain mDialog = new HintDialogMain(TestActivity.this);
            mDialog.setHint("重置密码的邮已经发送至您的邮箱");
            mDialog.addButton("知道了", "#0682e6", v1 -> {
                mDialog.dismiss();
                finish();
            });
            mDialog.show();
        });

        add("对话框12", v -> {

            HintDialogMain mExitDialog = new HintDialogMain(TestActivity.this);
            mExitDialog.setHint("确定退出考试?");
            mExitDialog.addButton("确定", "#0682e6", v1 -> {
                finish();
                mExitDialog.dismiss();
            });
            mExitDialog.addButton("取消", "#666666", v1 -> mExitDialog.dismiss());
            mExitDialog.show();

        });
        add("对话框13", v -> {

            HintDialogMain mSubDialog = new HintDialogMain(TestActivity.this);
            mSubDialog.setHint("还有几题");

            mSubDialog.addButton("确定", "#0682e6", v1 -> {
                mSubDialog.dismiss();
            });
            mSubDialog.addButton("取消", "#666666", v1 -> mSubDialog.dismiss());
            mSubDialog.show();

        });
        add("对话框21", v -> {
            HintDialogSec mDialog = new HintDialogSec(TestActivity.this);
            mDialog = new HintDialogSec(TestActivity.this);
            HintDialogSec finalMDialog = mDialog;
            mDialog.addButton("确定", "#0682e6", v1 -> finalMDialog.dismiss());
            mDialog.show();
        });
        add("对话框22", v -> {
            HintDialogSec mCloseDialog = new HintDialogSec(TestActivity.this);
            mCloseDialog.setMainHint(getString(R.string.exam_five_min));
            mCloseDialog.setSecHint(2 + getString(R.string.exam_xs_close));
            // FIXME: 2017/6/13 倒数
            mCloseDialog.addButton("确定", "#0682e6", v1 -> mCloseDialog.dismiss());
            mCloseDialog.show();
        });
        add("对话框23", v -> {
            HintDialogSec mSubmitDialog = new HintDialogSec(TestActivity.this);
            mSubmitDialog.setMainHint(getString(R.string.exam_end));
            mSubmitDialog.setSecHint(getString(R.string.exam_submit));
            mSubmitDialog.setCancelable(false);
            mSubmitDialog.addButton("确定", "#0682e6", v1 -> {
                mSubmitDialog.dismiss();
            });
        });
        add("对话框24", v -> {
            HintDialogSec mDialog = new HintDialogSec(TestActivity.this);
            mDialog.addButton("知道了", "#0682e6", v1 -> mDialog.dismiss());

            mDialog.setMainHint("请在系统设置中，打开“隐私-定位服务");
            mDialog.setSecHint("并允许定位服务");
            mDialog.show();
        });


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

package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import lib.ys.ui.dialog.DialogEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import yy.doctor.Constants.DateUnit;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.HintDialogSec;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.popup.TopicPopup;
import yy.doctor.sp.SpApp;
import yy.doctor.util.ExamCount;
import yy.doctor.util.ExamCount.OnCountListener;
import yy.doctor.util.Util;

/**
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class ExamTopicActivity extends BaseTopicActivity implements OnCountListener {

    private static final int KTextSizeDp = 16;
    private static final long KFiveMin = TimeUnit.MINUTES.toSeconds(5);

    private final int KXClose = 2; // X秒后自动关闭

    private TextView mTvTime;

    private HintDialogSec mCloseDialog; // 离考试结束的提示框
    private HintDialogSec mSubmitDialog; // 提交的提示框
    private long mUseTime; // 剩余做题的时间
    private TopicPopup mTopicPopup;

    public static void nav(Context context, String meetId, String moduleId, Intro intro) {
        Intent i = new Intent(context, ExamTopicActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId)
                .putExtra(Extra.KData, intro);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        super.initData();

        mIntro = (Intro) getIntent().getSerializableExtra(Extra.KData);
        mUseTime = mIntro.getLong(TIntro.usetime) * TimeUnit.MINUTES.toSeconds(1);

        long surplusTime = ExamCount.inst().getSurplusTime();
        mUseTime = mUseTime > surplusTime ? surplusTime : mUseTime;

        ExamCount.inst().setOnCountListener(this);
        ExamCount.inst().start(mUseTime);

        if (mUseTime <= KFiveMin) {
            last5((int) (mUseTime / TimeUnit.MINUTES.toSeconds(1)));
        }

        initFrag();
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        bar.addTextViewMid(R.string.exam);

        //默认显示,外加倒计时
        mTvTime = new TextView(ExamTopicActivity.this);
        mTvTime.setText(Util.format(mUseTime, DateUnit.hour));
        mTvTime.setGravity(Gravity.CENTER);
        mTvTime.setTextColor(Color.WHITE);
        mTvTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, KTextSizeDp);
        mTvTime.setPadding(0, 0, fitDp(12), 0);

        bar.addViewRight(mTvTime, null);
    }

    @Override
    public void setViews() {
        super.setViews();

        // 第一次进入考试时提示
        if (SpApp.inst().ifFirstEnterExam()) {
            runOnUIThread(() -> {
                mTopicPopup = new TopicPopup(ExamTopicActivity.this);
                mTopicPopup.showAtLocation(getNavBar(), Gravity.CENTER, 0, 0);
                SpApp.inst().saveEnterExam();
            }, 300);
        }

        initFirstGv();
    }

    @Override
    protected void submit() {
        Intent i = new Intent(ExamTopicActivity.this, ExamEndActivity.class)
                .putExtra(Extra.KMeetId, mMeetId)
                .putExtra(Extra.KModuleId, mModuleId)
                .putExtra(Extra.KPaperId, mPaperId)
                .putExtra(Extra.KPass, mIntro.getInt(TIntro.passScore))
                .putExtra(Extra.KNum, mIntro.getInt(TIntro.resitTimes) - mIntro.getInt(TIntro.finishTimes) - 1)
                .putExtra(Extra.KData, getAnswer(mAllTopics));
        LaunchUtil.startActivity(ExamTopicActivity.this, i);
        finish();
    }

    @Override
    protected String setDialogHint(int noFinish) {
        if (noFinish > 0) {
            //还有没作答
            return "还有" + noFinish + "题未完成,继续提交将不得分是否确认提交答卷?";
        } else {
            //全部作答完了
            return "确定提交答案?";
        }
    }

    // 小于5分钟提示
    private void last5(int last) {
        if (last <= 0) {
            last = 1;
        }
        mCloseDialog = new HintDialogSec(ExamTopicActivity.this);
        mCloseDialog.setMainHint(getString(R.string.exam_finish) + last + getString(R.string.minute));
        mCloseDialog.setSecHint(KXClose + getString(R.string.exam_xs_close));
        mCloseDialog.setCountHint(getString(R.string.exam_xs_close));
        mCloseDialog.addButton("确定", v -> mCloseDialog.dismiss());
        mCloseDialog.start(KXClose);
        mCloseDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ExamCount.inst().stop();
        recycleDialog(mCloseDialog);
        recycleDialog(mSubmitDialog);
        if (mTopicPopup != null) {
            mTopicPopup.dismiss();
            mTopicPopup = null;
        }
    }

    private void recycleDialog(DialogEx dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    @Override
    public void onCount(long remainCount) {
        mTvTime.setText(Util.format(remainCount, DateUnit.hour));
        if (remainCount != 0) {
            if (remainCount == KFiveMin) {
                // 剩余5分钟提示
                last5(5);
            }
        } else {
            // 考试结束强制提交
            mSubmitDialog = new HintDialogSec(ExamTopicActivity.this);
            mSubmitDialog.setMainHint(R.string.exam_end);
            mSubmitDialog.setSecHint(R.string.exam_submit);
            mSubmitDialog.setCancelable(false);
            mSubmitDialog.addButton(R.string.confirm, v -> {
                mSubmitDialog.dismiss();
                submit();
            });
            mSubmitDialog.show();
        }
    }

}

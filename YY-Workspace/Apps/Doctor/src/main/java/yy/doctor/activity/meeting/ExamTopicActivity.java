package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import lib.ys.ui.dialog.DialogEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.Constants.DateUnit;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.CommonOneDialog;
import yy.doctor.dialog.CommonTwoDialog;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.util.Util;

/**
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class ExamTopicActivity extends BaseTopicActivity implements OnCountDownListener {

    private static final int KTextSizeDp = 16;
    private static final long KFiveMin = TimeUnit.MINUTES.toSeconds(5);

    private final int KXClose = 2;//X秒后自动关闭

    private long mAllUseTime; // 剩余做题的时间
    private long mUseTime; // 做题的时间
    private CountDown mCountDown;

    private TextView mTvTime;

    private CommonOneDialog mCloseDialog;//离考试结束的提示框
    private CommonOneDialog mSubmitDialog;
    private CommonTwoDialog mSubDialog;

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
        mAllUseTime = mIntro.getInt(TIntro.time) * TimeUnit.MINUTES.toSeconds(1);
        initFrag();
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        mTvLeft.setText("考试");

        //默认显示,外加倒计时
        mTvTime = new TextView(ExamTopicActivity.this);
        mTvTime.setText(Util.formatTime(mAllUseTime, DateUnit.hour));
        mTvTime.setGravity(Gravity.CENTER);
        mTvTime.setTextColor(Color.WHITE);
        mTvTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, KTextSizeDp);

        mCountDown = new CountDown(mAllUseTime);
        mCountDown.setListener(this);
        mCountDown.start();

        bar.addViewMid(mTvTime);
    }

    @Override
    public void setViews() {
        super.setViews();

        initFirstGv();
    }

    @Override
    protected void topicCaseVisibility(boolean showState) {
        super.topicCaseVisibility(showState);
        if (getTopicCaseShow()) {
            mTvLeft.setText("题目");
            goneView(mTvTime);
        } else {
            mTvLeft.setText("考试");
            showView(mTvTime);
        }
    }

    @Override
    protected void trySubmit(int noFinish) {
        //考试时间未完
        if (mSubDialog == null) {
            mSubDialog = new CommonTwoDialog(ExamTopicActivity.this);
        }
        if (noFinish > 0) {
            //还有没作答
            mSubDialog.setTvMainHint("还有" + noFinish + "题未完成,继续提交将不得分")
                    .setTvSecondaryHint("是否确认提交答卷?");
        } else {
            //全部作答完了
            mSubDialog.setTvMainHint("确定提交答案?")
                    .hideSecond();
        }
        mSubDialog.mTvLeft(getString(R.string.exam_submit_sure))
                .mTvRight(getString(R.string.exam_continue))
                .setLayoutListener(new CommonTwoDialog.OnLayoutListener() {
                    @Override
                    public void leftClick(View v) {
                        submit();
                    }

                    @Override
                    public void rightClick(View v) {

                    }
                });
        mSubDialog.show();
    }

    @Override
    protected void submit() {
        Intent i = new Intent(ExamTopicActivity.this, ExamEndActivity.class)
                .putExtra(Extra.KMeetId, mMeetId)
                .putExtra(Extra.KModuleId, mModuleId)
                .putExtra(Extra.KPaperId, mPaperId)
                .putExtra(Extra.KTime, mUseTime)
                .putExtra(Extra.KData, getAnswer(mAllTopics));
        LaunchUtil.startActivity(ExamTopicActivity.this, i);
        finish();
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
    public void onCountDown(long remainCount) {
        mUseTime = mAllUseTime - remainCount;
        mTvTime.setText(Util.formatTime(remainCount, DateUnit.hour));
        if (remainCount != 0) {
            if (remainCount == KFiveMin) {
                // 剩余5分钟
                mCloseDialog = new CommonOneDialog(ExamTopicActivity.this) {
                    @Override
                    public void close(Long aLong) {
                        setTvSecondaryHint(aLong + getString(R.string.exam_xs_close));
                    }
                }
                        .setTvMainHint(getString(R.string.exam_five_min))
                        .setTvSecondaryHint(KXClose + getString(R.string.exam_xs_close));
                mCloseDialog.start(KXClose);
            }
        } else {
            mSubmitDialog = new CommonOneDialog(ExamTopicActivity.this)
                    .setTvMainHint(getString(R.string.exam_end))
                    .setTvSecondaryHint(getString(R.string.exam_submit));
            mSubmitDialog.setCancelable(false);
            mSubmitDialog.setOnClickListener(v -> {
                mSubmitDialog.dismiss();
                submit();
            });
            mSubmitDialog.show();
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCountDown != null) {
            mCountDown.stop();
        }

        recycleDialog(mCloseDialog);
        recycleDialog(mSubmitDialog);
        recycleDialog(mSubDialog);
    }

}

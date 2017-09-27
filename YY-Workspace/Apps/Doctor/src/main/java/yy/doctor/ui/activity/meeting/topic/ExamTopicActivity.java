package yy.doctor.ui.activity.meeting.topic;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import yy.doctor.Constants.DateUnit;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.HintDialogSec;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.model.meet.exam.Paper.TPaper;
import yy.doctor.popup.TopicPopup;
import yy.doctor.sp.SpApp;
import yy.doctor.util.ExamCount;
import yy.doctor.util.ExamCount.OnCountListener;
import yy.doctor.util.Time;
import yy.doctor.util.Util;

/**
 * 考试答题界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */
public class ExamTopicActivity extends BaseTopicActivity implements OnCountListener {

    private final long KLastHint = TimeUnit.MINUTES.toSeconds(5); // 剩余多少提示
    private final int KXClose = 2; // X秒后自动关闭

    private TextView mTvTime;

    private long mUseTime; // 剩余做题的时间
    private TopicPopup mTopicPopup; // App第一次考试

    private boolean mShouldHint; // 是否可以提示剩余时间

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

        mShouldHint = true;
        mIntro = (Intro) getIntent().getSerializableExtra(Extra.KData);
        long surplusTime = ExamCount.inst().getRemainTime();
        mUseTime = mIntro.getLong(TIntro.usetime) * TimeUnit.MINUTES.toSeconds(1);
        mUseTime = mUseTime > surplusTime ? surplusTime : mUseTime;

        ExamCount.inst().setOnCountListener(this);
        ExamCount.inst().start(mUseTime);

        initFrag();
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        bar.addTextViewMid(R.string.exam);

        //默认显示,外加倒计时
        mTvTime = bar.addTextViewRight(Time.secondFormat(mUseTime, DateUnit.hour), null);
    }

    @Override
    public void setViews() {
        super.setViews();

        if (SpApp.inst().isFirstExam()) {
            // 第一次进入考试时提示
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                mTopicPopup = new TopicPopup(ExamTopicActivity.this);
                mTopicPopup.showAtLocation(getNavBar(), Gravity.CENTER, 0, 0);
                SpApp.inst().noFirstExam();
                removeOnGlobalLayoutListener(this);
                }
            });
        }

        initFirstGv();
    }

    @Override
    protected void submit() {
        if (Util.noNetwork()) {
            return;
        }
        ExamEndActivityRouter.create()
                .meetId(mMeetId)
                .moduleId(mModuleId)
                .paperId(mPaper.getString(TPaper.id))
                .pass(mIntro.getInt(TIntro.passScore))
                .count(mIntro.getInt(TIntro.resitTimes) - mIntro.getInt(TIntro.finishTimes) - 1)
                .topics(mTopics)
                .route(ExamTopicActivity.this);
        finish();
    }

    @Override
    protected String submitHint(int noFinish) {
        if (noFinish > 0) {
            //还有没作答
            return String.format(getString(R.string.exam_submit_hint_no_finish), noFinish);
        } else {
            //全部作答完了
            return getString(R.string.exam_submit_hint_finish);
        }
    }

    @Override
    protected String getExitHint() {
        return "考试正在进行中，如果退出，您的答题将失效。";
    }

    @Override
    public void onCount(long remainCount) {
        mTvTime.setText(Time.secondFormat(remainCount, DateUnit.hour));
        if (remainCount != 0) {
            // 没结束
            if (mShouldHint && remainCount <= KLastHint) {
                // 剩余5分钟提示
                lastHint(remainCount);
                mShouldHint = false;
            }
        } else {
            // 考试结束强制提交
            HintDialogSec d = new HintDialogSec(ExamTopicActivity.this);
            d.setMainHint(R.string.exam_end);
            d.setSecHint(R.string.exam_submit_confirm);
            d.setCancelable(false);
            d.addBlueButton(R.string.confirm, v -> submit());
            d.show();
        }
    }

    // 快结束的提示(只提示一次)
    private void lastHint(long last) {
        last /= TimeUnit.MINUTES.toSeconds(1);
        if (last <= 0) {
            // 最少提示一分钟
            last = 1;
        }
        HintDialogSec d = new HintDialogSec(ExamTopicActivity.this);
        d.setMainHint(String.format(getString(R.string.exam_finish), last));
        d.setSecHint(KXClose + getString(R.string.exam_xs_close));
        d.setCountHint(R.string.exam_xs_close);
        d.addBlueButton(R.string.confirm);
        d.start(KXClose);
        d.show();
    }

    @Override
    public void finish() {
        super.finish();

        ExamCount.inst().remove();
        if (mTopicPopup != null) {
            mTopicPopup.dismiss();
        }
    }

}

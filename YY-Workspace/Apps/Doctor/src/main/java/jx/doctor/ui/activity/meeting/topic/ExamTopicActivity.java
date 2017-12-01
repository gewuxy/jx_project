package jx.doctor.ui.activity.meeting.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import jx.doctor.Constants.DateUnit;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.dialog.HintDialogSec;
import jx.doctor.model.meet.topic.TopicIntro;
import jx.doctor.model.meet.topic.TopicIntro.TTopicIntro;
import jx.doctor.model.meet.topic.TopicPaper.TTopicPaper;
import jx.doctor.sp.SpApp;
import jx.doctor.util.ExamCount;
import jx.doctor.util.ExamCount.OnCountListener;
import jx.doctor.util.Time;
import jx.doctor.util.Util;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.LayoutUtil;

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

    private boolean mShouldHint; // 是否可以提示剩余时间

    public static void nav(Context context, String meetId, String moduleId, TopicIntro topicIntro) {
        Intent i = new Intent(context, ExamTopicActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId)
                .putExtra(Extra.KData, topicIntro);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        mShouldHint = true;
        mTopicIntro = (TopicIntro) getIntent().getSerializableExtra(Extra.KData);
        long surplusTime = ExamCount.inst().getRemainTime();
        mUseTime = mTopicIntro.getLong(TTopicIntro.usetime) * TimeUnit.MINUTES.toSeconds(1);
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
                    View view = inflate(R.layout.layout_topic_hint);
                    view.setOnClickListener(v -> goneView(v));
                    getWindow().addContentView(view, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT));
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
                .paperId(mTopicPaper.getString(TTopicPaper.id))
                .pass(mTopicIntro.getInt(TTopicIntro.passScore))
                .count(mTopicIntro.getInt(TTopicIntro.resitTimes) - mTopicIntro.getInt(TTopicIntro.finishTimes) - 1)
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
    }

}

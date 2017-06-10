package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TimeUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.CommonOneDialog;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.model.meet.exam.Paper;
import yy.doctor.model.meet.exam.Paper.TPaper;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 考试介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class ExamIntroActivity extends BaseActivity implements OnCountDownListener {

    private Paper mPaper; // 本次考试试题信息
    private Intro mIntro;
    private String mHost; // 会议主办方
    private String mMeetId; // 会议ID
    private String mModuleId; // 模块ID

    private long mStartTime; // 考试开始时间
    private long mEndTime; // 考试结束时间
    private long mCurTime; // 服务器当前时间

    private TextView mTvTitle; // 试卷名称
    private TextView mTvHost; // 主办方
    private TextView mTvCount; // 总题数
    private TextView mTvTime; // 考试时间
    private TextView mTvState; // 考试状态

    private boolean mCanStart; // 是否能开始考试

    private CommonOneDialog mStartDialog; // 考试未开始的提示框
    private CommonOneDialog mEndDialog; // 考试已结束的提示框
    private CountDown mCountDown;

    @StringDef({
            TimeFormat.simple_ymd,
            TimeFormat.from_y_to_m_24,
            TimeFormat.from_h_to_m_24,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface TimeFormat {
        String simple_ymd = "yyyy/MM/dd";
        String from_y_to_m_24 = "yyyy/MM/dd HH:mm";
        String from_h_to_m_24 = "HH:mm";
    }

    public static void nav(Context context, String meetId, String moduleId, String host) {
        Intent i = new Intent(context, ExamIntroActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId)
                .putExtra(Extra.KData, host);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
        mHost = getIntent().getStringExtra(Extra.KData);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_exam_intro;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "考试", this);
    }

    @Override
    public void findViews() {
        mTvTitle = findView(R.id.exam_intro_tv_title);
        mTvHost = findView(R.id.exam_intro_tv_host);
        mTvCount = findView(R.id.exam_intro_tv_count);
        mTvTime = findView(R.id.exam_intro_tv_time);
        mTvState = findView(R.id.exam_intro_tv_state);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.exam_intro_tv_start);

        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.toExam(mMeetId, mModuleId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Intro.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Result<Intro> r = (Result<Intro>) result;
        if (r.isSucceed()) {
            mIntro = r.getData();
            mPaper = mIntro.getEv(TIntro.paper);

            //获取起始结束时间
            mStartTime = mIntro.getLong(Intro.TIntro.startTime);
            mEndTime = mIntro.getLong(Intro.TIntro.endTime);
            mCurTime = mIntro.getLong(Intro.TIntro.serverTime);

            mCanStart = mStartTime <= mCurTime && mCurTime < mEndTime;
            if (mEndTime > mCurTime) {
                // 未结束的话开始计时
                Long maxCount = (mEndTime - mCurTime) / TimeUnit.MINUTES.toMillis(1);
                mCountDown = new CountDown(maxCount, TimeUnit.MINUTES);
                mCountDown.setListener(this);
                mCountDown.start();
            }

            mTvTitle.setText(mPaper.getString(TPaper.name));
            mTvHost.setText(getString(R.string.exam_host) + mHost);
            mTvCount.setText(mPaper.getList(TPaper.questions).size() + "道题目");
            mTvTime.setText(getTime(mStartTime, mEndTime));
            changeExamState();
        } else {
            showToast(r.getError());
        }
    }

    /**
     * 获取考试状态
     */
    private void changeExamState() {
        String text = null;
        @ColorInt int color;
        if (mCurTime < mStartTime) {
            // 未开始
            text = "未开始";
            color = ResLoader.getColor(R.color.text_01b557);
        } else if (mCurTime > mStartTime) {
            // 结束
            text = "进行中";
            color = ResLoader.getColor(R.color.text_e6600e);
        } else {
            // 进行中
            text = "精彩回顾";
            color = ResLoader.getColor(R.color.text_5cb0de);
        }
        mTvState.setTextColor(color);
        mTvState.setText(text);
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_intro_tv_start:
                // 点击开始考试
                if (mCanStart) {
                    // TODO: 2017/6/10
                    ExamTopicActivity.nav(ExamIntroActivity.this, mMeetId, mModuleId, mIntro);
                    finish();
                } else if (mStartTime > mCurTime) {
                    // 考试未开始
                    mStartDialog = new CommonOneDialog(ExamIntroActivity.this)
                            .setTvMainHint(getString(R.string.exam_no_start))
                            .setTvSecondaryHint(getString(R.string.exam_participation));
                    mStartDialog.show();
                } else {
                    // 考试结束
                    mEndDialog = new CommonOneDialog(ExamIntroActivity.this)
                            .setTvMainHint(getString(R.string.exam_end))
                            .setTvSecondaryHint(getString(R.string.exam_contact));
                    mEndDialog.show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStartDialog != null) {
            if (mStartDialog.isShowing()) {
                mStartDialog.dismiss();
            }
            mStartDialog = null;
        }
        if (mEndDialog != null) {
            if (mEndDialog.isShowing()) {
                mEndDialog.dismiss();
            }
            mEndDialog = null;
        }

        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            // 倒数结束(考试结束)
            mCanStart = false;
            changeExamState();
        } else {
            // 每次的倒数
            mCurTime += TimeUnit.MINUTES.toMillis(1);
            if (!mCanStart && mCurTime >= mStartTime) {
                // 未开始到进行中
                mCanStart = true;
                changeExamState();
            }
        }
    }

    @Override
    public void onCountDownErr() {
    }

    /**
     * 按格式显示起始结束时间
     */
    private String getTime(long startTime, long endTime) {
        StringBuilder time = null;
        String startDate = TimeUtil.formatMilli(startTime, TimeFormat.simple_ymd);
        String endDate = TimeUtil.formatMilli(endTime, TimeFormat.simple_ymd);
        if (startDate.equals(endDate)) {
            //同一天
            time = new StringBuilder(startDate)
                    .append(" ")
                    .append(TimeUtil.formatMilli(startTime, TimeFormat.from_h_to_m_24))
                    .append("~")
                    .append(TimeUtil.formatMilli(endTime, TimeFormat.from_h_to_m_24));
        } else {
            //不在同一天
            time = new StringBuilder()
                    .append(TimeUtil.formatMilli(startTime, TimeFormat.from_y_to_m_24))
                    .append("~")
                    .append(TimeUtil.formatMilli(endTime, TimeFormat.from_y_to_m_24));
        }
        return time.toString();
    }
}

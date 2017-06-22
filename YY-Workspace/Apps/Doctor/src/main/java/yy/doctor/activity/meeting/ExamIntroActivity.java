package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TimeUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.HintDialogSec;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.model.meet.exam.Paper;
import yy.doctor.model.meet.exam.Paper.TPaper;
import yy.doctor.model.meet.exam.Topic;
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
    private TextView mTvScore; // 过往成绩

    private boolean mCanStart; // 是否能开始考试

    private HintDialogSec mDialog; // 提示不能考试的原因
    private CountDown mCountDown; // FIXME:改为Handler

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    mCanStart = true;
                    break;
                case 1:
                case 2:
                    mCanStart = false;
                    break;
            }
        }
    };

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
        mTvScore = findView(R.id.exam_intro_tv_score);
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
        Result<Intro> r = (Result<Intro>) result;
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            mIntro = r.getData();
            mPaper = mIntro.getEv(TIntro.paper);

            //获取起始结束时间
            mStartTime = mIntro.getLong(TIntro.startTime);
            mEndTime = mIntro.getLong(TIntro.endTime);
            mCurTime = mIntro.getLong(TIntro.serverTime);



            if (mIntro.getBoolean(TIntro.finished)) {
                mTvScore.setText("过往成绩 : " + mIntro.getInt(TIntro.score) + "分");
            } else {
                hideView(mTvScore);
            }

            mCanStart = mStartTime <= mCurTime && mCurTime < mEndTime;
            YSLog.d(TAG, mCanStart + "");
            if (mStartTime >= mCurTime) {     //还没到考试时间进去

                Long minCount = mStartTime - mCurTime;   //注意传值是毫秒的单位
                handler.sendEmptyMessageDelayed(0, minCount);//到考试时间了发消息给handle
                Long totalCount = mEndTime - mStartTime;
                handler.sendEmptyMessageDelayed(1, minCount + totalCount);//考试时间结束了发消息

                // 未结束的话开始计时
//                mCountDown = new CountDown(maxCount, TimeUnit.MINUTES);
//                mCountDown.setListener(this);
//                mCountDown.start();

            } else if (mStartTime <= mCurTime && mCurTime < mEndTime) {//过了开考时间还没到结束时间
                Long maxCount = mEndTime - mCurTime;
                handler.sendEmptyMessageDelayed(2, maxCount);//考试时间结束发消息
            }

            mTvTitle.setText(mPaper.getString(TPaper.name));
            mTvHost.setText(getString(R.string.exam_host) + mHost);
            List<Topic> topics = mPaper.getList(TPaper.questions);
            if (topics != null && topics.size() > 0) {
                mTvCount.setText(topics.size() + "道题目");
            }
            mTvTime.setText(getTime(mStartTime, mEndTime));
        } else {
            setViewState(ViewState.error);
            showToast(r.getError());
        }
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
                    // 时间段考试
                    long useTime = mIntro.getLong(TIntro.usetime) * TimeUnit.MINUTES.toMillis(1);
                    long surplusTime = mEndTime - mCurTime; // 离考试结束的时间
                    if (useTime < 0) {
                        // 服务器没给时间
                        long examTime = mEndTime - mStartTime; // 考试时间段
                        mIntro.put(TIntro.time, examTime < surplusTime ? examTime : surplusTime);
                    } else {
                        mIntro.put(TIntro.time, surplusTime < useTime ? surplusTime : useTime);
                    }
                    ExamTopicActivity.nav(ExamIntroActivity.this, mMeetId, mModuleId, mIntro);
                    finish();
                } else {
                    if (mDialog == null) {
                        mDialog = new HintDialogSec(ExamIntroActivity.this);
                        mDialog.addButton("确定", v1 -> mDialog.dismiss());
                    }

                    if (mStartTime > mCurTime) {
                        // 考试未开始
                        mDialog.setMainHint(getString(R.string.exam_no_start));
                        mDialog.setSecHint(getString(R.string.exam_participation));
                    } else {
                        // 考试结束
                        mDialog.setMainHint(getString(R.string.exam_end));
                        mDialog.setSecHint(getString(R.string.exam_contact));
                    }
                    mDialog.show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mDialog = null;
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
        } else {
            // 每次的倒数
            mCurTime += TimeUnit.MINUTES.toMillis(1);
            if (!mCanStart && mCurTime >= mStartTime) {
                // 未开始到进行中
                mCanStart = true;
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

package yy.doctor.ui.activity.meeting;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.dialog.HintDialogSec;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.model.meet.exam.Paper;
import yy.doctor.model.meet.exam.Paper.TPaper;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.ExamCount;

/**
 * 考试介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */
@Route
public class ExamIntroActivity extends BaseActivity {

    private Intro mIntro; // 考试信息

    @Arg(optional = true)
    String mHost;

    @Arg(optional = true)
    String mMeetId;

    @Arg(optional = true)
    String mModuleId;

    private long mStartTime; // 考试开始时间
    private long mEndTime; // 考试结束时间

    private TextView mTvTitle; // 试卷名称
    private TextView mTvHost; // 主办方
    private TextView mTvCount; // 总题数
    private TextView mTvTime; // 考试时间
    private TextView mTvScore; // 过往成绩

    private boolean mCanStart; // 是否能开始考试

    private HintDialogSec mDialog; // 提示不能考试的原因

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

    @Override
    public void initData() {
        notify(NotifyType.study_start);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_exam_intro;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.mipmap.nav_bar_ic_back, v -> {
            // 没有进入考试
            // 记录模块时间
            notify(NotifyType.study_end);
            // 停止倒计时
            ExamCount.inst().remove();
            finish();
        });
        bar.addTextViewMid(R.string.exam);
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

        getDataFromNet();
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

            // 获取起始结束时间
            mStartTime = mIntro.getLong(TIntro.startTime);
            mEndTime = mIntro.getLong(TIntro.endTime);
            long curTime = mIntro.getLong(TIntro.serverTime); // 服务器当前时间

            if (mIntro.getBoolean(TIntro.finished)) {
                mTvScore.setText(String.format("过往成绩 : %d 分", mIntro.getInt(TIntro.score, 0)));
            } else {
                hideView(mTvScore);
            }

            long difEnd = mEndTime - curTime; // 离考试结束还有多少时间
            if (difEnd > 0) {
                // 考试没结束
                ExamCount.inst().start(difEnd / TimeUnit.SECONDS.toMillis(1));
            }

            mCanStart = mStartTime <= curTime && difEnd > 0; // (mCurTime < mEndTime) 当前能不能考试

            Paper paper = mIntro.getEv(TIntro.paper);
            mTvTitle.setText(paper.getString(TPaper.name));
            mTvHost.setText(getString(R.string.exam_host) + mHost);
            List<Topic> topics = paper.getList(TPaper.questions);
            if (topics != null && topics.size() > 0) {
                mTvCount.setText(String.format("%d道题目", topics.size()));
            }
            mTvTime.setText(format(mStartTime, mEndTime));
        } else {
            setViewState(ViewState.error);
            showToast(r.getMessage());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            getDataFromNet();
        }
        return true;
    }

    private void getDataFromNet() {
        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.toExam(mMeetId, mModuleId));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_intro_tv_start: {
                // 点击开始考试
                if (mIntro.getInt(TIntro.score, 0) >= mIntro.getInt(TIntro.passScore)) {
                    // 及格不让再考
                    showToast(R.string.finish_exam);
                    return;
                }
                if (mCanStart) {
                    // 考试进行中
                    if (mIntro.getInt(TIntro.resitTimes) > mIntro.getInt(TIntro.finishTimes)) {
                        // 还有考试次数
                        ExamTopicActivity.nav(ExamIntroActivity.this, mMeetId, mModuleId, mIntro);
                        finish();
                    } else {
                        showToast(R.string.finish_exam);
                    }
                } else {
                    mDialog = new HintDialogSec(ExamIntroActivity.this);
                    mDialog.addButton(R.string.confirm, v1 -> mDialog.dismiss());
                    mDialog.setMainHint(R.string.exam_end);
                    mDialog.setSecHint(R.string.exam_contact);
                    mDialog.show();
                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // 记录模块时间
        notify(NotifyType.study_end);
        // 停止倒计时
        ExamCount.inst().remove();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // PS: 有可能下一页再统计时间且不停止考试的倒计时
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * 按格式显示起始结束时间
     */
    private String format(long startTime, long endTime) {
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

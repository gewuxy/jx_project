package jx.doctor.ui.activity.meeting.topic;

import android.os.Bundle;
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
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeFormatter;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import jx.doctor.R;
import jx.doctor.dialog.HintDialogSec;
import jx.doctor.model.meet.topic.Topic;
import jx.doctor.model.meet.topic.TopicIntro;
import jx.doctor.model.meet.topic.TopicIntro.TTopicIntro;
import jx.doctor.model.meet.topic.TopicPaper;
import jx.doctor.model.meet.topic.TopicPaper.TTopicPaper;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.util.ExamCount;

/**
 * 考试介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */
@Route
public class ExamIntroActivity extends BaseActivity {

    @Arg(opt = true)
    String mHost;

    @Arg(opt = true)
    String mMeetId;

    @Arg(opt = true)
    String mModuleId;

    private long mStartTime; // 考试开始时间
    private long mEndTime; // 考试结束时间

    private TextView mTvTitle; // 试卷名称
    private TextView mTvHost; // 主办方
    private TextView mTvCount; // 总题数
    private TextView mTvTime; // 考试时间
    private TextView mTvScore; // 过往成绩

    private TopicIntro mTopicIntro; // 考试信息

    private boolean mCanStart; // 是否能开始考试

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
        bar.addViewLeft(R.drawable.nav_bar_ic_back, v -> {
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

        refresh(RefreshWay.embed);
        getDataFromNet();
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), TopicIntro.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            mTopicIntro = (TopicIntro) r.getData();

            // 获取起始结束时间
            mStartTime = mTopicIntro.getLong(TTopicIntro.startTime);
            mEndTime = mTopicIntro.getLong(TTopicIntro.endTime);
            long curTime = mTopicIntro.getLong(TTopicIntro.serverTime); // 服务器当前时间

            if (mTopicIntro.getBoolean(TTopicIntro.finished)) {
                mTvScore.setText(String.format("过往成绩 : %d 分", mTopicIntro.getInt(TTopicIntro.score, 0)));
            } else {
                hideView(mTvScore);
            }

            long difEnd = mEndTime - curTime; // 离考试结束还有多少时间
            if (difEnd > 0) {
                // 考试没结束
                ExamCount.inst().start(difEnd / TimeUnit.SECONDS.toMillis(1));
            }

            mCanStart = mStartTime <= curTime && difEnd > 0; // (mCurTime < mEndTime) 当前能不能考试

            TopicPaper topicPaper = mTopicIntro.get(TTopicIntro.paper);
            mTvTitle.setText(topicPaper.getString(TTopicPaper.name));
            mTvHost.setText(getString(R.string.exam_host) + mHost);
            List<Topic> topics = topicPaper.getList(TTopicPaper.questions);
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
            refresh(RefreshWay.embed);
            getDataFromNet();
        }
        return true;
    }

    private void getDataFromNet() {
        exeNetworkReq(MeetAPI.toExam(mMeetId, mModuleId).build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_intro_tv_start: {
                // 点击开始考试
                if (mTopicIntro.getInt(TTopicIntro.score, 0) >= mTopicIntro.getInt(TTopicIntro.passScore)) {
                    // 及格不让再考
                    showToast(R.string.finish_exam);
                    return;
                }
                if (mCanStart) {
                    // 考试进行中
                    if (mTopicIntro.getInt(TTopicIntro.resitTimes) > mTopicIntro.getInt(TTopicIntro.finishTimes)) {
                        // 还有考试次数
                        ExamTopicActivity.nav(ExamIntroActivity.this, mMeetId, mModuleId, mTopicIntro);
                        finish();
                    } else {
                        showToast(R.string.finish_exam);
                    }
                } else {
                    // 提示不能考试的原因
                    HintDialogSec dialog = new HintDialogSec(ExamIntroActivity.this);
                    dialog.addBlueButton(R.string.confirm);
                    dialog.setMainHint(R.string.exam_end);
                    dialog.setSecHint(R.string.exam_contact);
                    dialog.show();
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

    /**
     * 按格式显示起始结束时间
     */
    private String format(long startTime, long endTime) {
        StringBuilder time = null;
        String startDate = TimeFormatter.milli(startTime, TimeFormat.simple_ymd);
        String endDate = TimeFormatter.milli(endTime, TimeFormat.simple_ymd);
        if (startDate.equals(endDate)) {
            //同一天
            time = new StringBuilder(startDate)
                    .append(" ")
                    .append(TimeFormatter.milli(startTime, TimeFormat.from_h_to_m_24))
                    .append("~")
                    .append(TimeFormatter.milli(endTime, TimeFormat.from_h_to_m_24));
        } else {
            //不在同一天
            time = new StringBuilder()
                    .append(TimeFormatter.milli(startTime, TimeFormat.from_y_to_m_24))
                    .append("~")
                    .append(TimeFormatter.milli(endTime, TimeFormat.from_y_to_m_24));
        }
        return time.toString();
    }

}

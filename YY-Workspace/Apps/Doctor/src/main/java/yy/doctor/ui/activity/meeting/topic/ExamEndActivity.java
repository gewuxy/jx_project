package yy.doctor.ui.activity.meeting.topic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.model.meet.topic.Topic;
import yy.doctor.model.meet.topic.TopicResult;
import yy.doctor.model.meet.topic.TopicResult.TTopicResult;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;
import yy.doctor.util.Util;

/**
 * 考试结果界面
 *
 * @author CaiXiang
 * @since 2017/4/28
 */
@Route
public class ExamEndActivity extends BaseActivity {

    @Arg(opt = true)
    String mMeetId;

    @Arg(opt = true)
    String mModuleId;

    @Arg(opt = true)
    String mPaperId; // 试卷Id

    @Arg(opt = true)
    int mCount; // 考试次数

    @Arg(opt = true)
    int mPass; // 及格线

    @Arg(opt = true)
    ArrayList<Topic> mTopics; // 试题(记录答案)

    private TextView mTvScore;
    private TextView mTvPass;
    private TextView mTvCount;
    private TextView mTvFinish;
    private View mLayout;

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_exam_end;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "考试结束", this);
    }

    @Override
    public void findViews() {
        mTvScore = findView(R.id.exam_end_tv_score);
        mTvPass = findView(R.id.exam_end_tv_pass);
        mLayout = findView(R.id.exam_end_layout_hint);
        mTvCount = findView(R.id.exam_end_tv_count);
        mTvFinish = findView(R.id.exam_end_tv_btn);
    }

    @Override
    public void setViews() {
        setOnClickListener(mTvFinish);

        mTvCount.setText(String.valueOf(mCount));
        mTvPass.setText(String.valueOf(mPass));

        refresh(RefreshWay.embed);
        getDataFromNet();
    }

    private void getDataFromNet() {
        NetworkReq r = MeetAPI.submitExam()
                .meetId(mMeetId)
                .moduleId(mModuleId)
                .paperId(mPaperId)
                .itemJson(Util.chooseToJson(mTopics))
                .build();
        exeNetworkReq(r);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), TopicResult.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            TopicResult topic = (TopicResult) r.getData();
            int score = topic.getInt(TTopicResult.score);
            if (score >= mPass) {
                // 及格隐藏
                hideView(mLayout);
            }
            mTvScore.setText(String.valueOf(score));
        } else {
            setViewState(ViewState.error);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_end_tv_btn:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();

        notify(NotifyType.study_end);
    }
}

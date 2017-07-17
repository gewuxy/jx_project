package yy.doctor.ui.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.meet.exam.Answer;
import yy.doctor.model.meet.exam.TopicResult;
import yy.doctor.model.meet.exam.TopicResult.TTopicResult;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 考试结果界面
 *
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ExamEndActivity extends BaseActivity {

    private String mMeetId;
    private String mModuleId;
    private String mPaperId; // 试卷Id
    private int mCount; // 考试次数
    private int mPass; // 及格线
    private List<Answer> mAnswers; // 答案

    private TextView mTvScore;
    private TextView mTvPass;
    private TextView mTvCount;
    private TextView mTvFinish;
    private View mLayout;

    @Override
    public void initData() {
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
        mPaperId = getIntent().getStringExtra(Extra.KPaperId);
        mCount = getIntent().getIntExtra(Extra.KNum, 0);
        mPass = getIntent().getIntExtra(Extra.KPass, 0);
        mAnswers = (List<Answer>) getIntent().getSerializableExtra(Extra.KData);
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

        // FIXME: 考试用时
        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.submitEx()
                .meetId(mMeetId)
                .moduleId(mModuleId)
                .paperId(mPaperId)
                .items(mAnswers)
                .builder());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), TopicResult.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<TopicResult> response = (Result<TopicResult>) result;
        if (response.isSucceed()) {
            setViewState(ViewState.normal);
            TopicResult r = response.getData();
            int score = r.getInt(TTopicResult.score);
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
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
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

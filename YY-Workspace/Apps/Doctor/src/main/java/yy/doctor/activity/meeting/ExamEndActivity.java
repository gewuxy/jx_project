package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.Constants.DateUnit;
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
    private long mTime; // 考试用时
    private List<Answer> mAnswers; // 答案

    private TextView mTvTime;
    private TextView mTvScore;
    private TextView mTvRight;
    private TextView mTvError;
    private TextView mTvFinish;

    @Override
    public void initData() {
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
        mPaperId = getIntent().getStringExtra(Extra.KPaperId);
        mTime = getIntent().getLongExtra(Extra.KTime, 0);
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
        mTvTime = findView(R.id.exam_end_tv_time);
        mTvScore = findView(R.id.exam_end_tv_score);
        mTvRight = findView(R.id.exam_end_tv_right_num);
        mTvError = findView(R.id.exam_end_tv_error_num);
        mTvFinish = findView(R.id.exam_end_tv_btn);
    }

    @Override
    public void setViews() {
        setOnClickListener(mTvFinish);

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
        setViewState(ViewState.normal);
        Result<TopicResult> response = (Result<TopicResult>) result;
        if (response.isSucceed()) {
            TopicResult r = response.getData();
            mTvScore.setText(r.getString(TTopicResult.score));
            mTvRight.setText(r.getString(TTopicResult.rightCount));
            mTvError.setText(r.getString(TTopicResult.errorCount));
            mTvTime.setText(Util.formatTime(mTime, DateUnit.minute));
        } else {
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
}

package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.network.error.NetError;
import lib.network.model.NetworkResponse;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Resp;
import yy.doctor.BuildConfig;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.exam.ExamTopicFrag;
import yy.doctor.model.exam.Exam;
import yy.doctor.model.exam.Exam.TExam;
import yy.doctor.model.exam.Paper;
import yy.doctor.model.exam.Paper.TExamPaper;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 考试介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class ExamIntroActivity extends BaseActivity {

    private static final int KExam = 8;
    private static final int KSurvey = 10;

    public static void nav(Context context, String[] info) {
        Intent i = new Intent(context, ExamTopicFrag.class);
        i.putExtra(Extra.KData, info);
        LaunchUtil.startActivity(context, i);
    }

    private TextView mTvTitle;  //试卷名称
    private TextView mTvHost;   //主办方
    private TextView mTvCount;  //总题数
    private TextView mTvTime;   //考试时间

    private String mModuleId;//模块ID
    private String mMeetId;//会议ID
    private Paper mPaper;//本次考试试题信息

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mModuleId = intent.getStringExtra("");
            mMeetId = intent.getStringExtra("");
        }
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
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.exam_intro_tv_start);

        if (BuildConfig.TEST) {
            mMeetId = "17042512131640894904";
            mModuleId = "10";
        }

        //TODO：开启就要显示还是再请求网络
        refresh(RefreshWay.embed);
        switch (Integer.valueOf(mModuleId.trim())) {
            case KExam:
                exeNetworkRequest(KExam, NetFactory.toExam(mMeetId, mModuleId));
                break;
            case KSurvey:
                exeNetworkRequest(KSurvey, NetFactory.toSurvey(mMeetId, mModuleId));
                break;
        }

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        return JsonParser.ev(nr.getText(), Exam.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Resp<Exam> r = (Resp<Exam>) result;
        mPaper = r.getData().getEv(TExam.paper);
        switch (id) {
            case KExam:
                mTvTitle.setText(mPaper.getString(TExamPaper.name));
                break;
            case KSurvey:
                mTvTitle.setText(mPaper.getString(TExamPaper.paperName));
                break;
        }
        List list = mPaper.getList(TExamPaper.questions);
        mTvCount.setText(list.size() + "道题");
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_intro_tv_start://开始考试
                startActivity(ExamTopicActivity.class);
                break;
            default:
                break;
        }
    }
}

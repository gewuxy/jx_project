package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TimeUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.BuildConfig;
import yy.doctor.Constants.ModuleId;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.exam.Intro;
import yy.doctor.model.exam.Intro.TIntro;
import yy.doctor.model.exam.Paper;
import yy.doctor.model.exam.Paper.TPaper;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 考试/问卷介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class IntroActivity extends BaseActivity {

    private static final int KExam = 8;
    private static final int KSurvey = 10;

    private TextView mTvTitle;  //试卷名称
    private TextView mTvHost;   //主办方
    private TextView mTvCount;  //总题数

    private String mModuleId;//模块ID
    private String mMeetId;//会议ID
    private Paper mPaper;//本次考试试题信息

    private TextView mTvTime;   //考试时间
    private long mStartTime;//考试开始时间
    private long mEndTime;//考试结束时间
    private long mNowTime;//服务器当前时间
    private DisposableSubscriber<Long> mSub;
    private boolean canStart;//是否能开始考试

    @StringDef({
            TimeFormat.simple_ymd,
            TimeFormat.from_y_to_m_24,
            TimeFormat.from_h_to_m_24,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeFormat {
        String simple_ymd = "yyyy/MM/dd"; // 2016-08-21
        String from_y_to_m_24 = "yyyy/MM/dd HH:mm"; // 24h
        String from_h_to_m_24 = "HH:mm";
    }

    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, IntroActivity.class);
        i.putExtra(Extra.KMeetId, meetId);
        i.putExtra(Extra.KModuleId, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mMeetId = intent.getStringExtra(Extra.KMeetId);
        mModuleId = intent.getStringExtra(Extra.KModuleId);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_exam_intro;
    }

    @Override
    public void initNavBar(NavBar bar) {
        String title = null;
        if (mModuleId.equals(ModuleId.KExam)) {
            title = "考试";
        } else {
            title = "问卷";
        }
        Util.addBackIcon(bar, title, this);
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
        }

        refresh(RefreshWay.embed);
        if (mModuleId.equals(ModuleId.KExam)) {
            exeNetworkReq(KExam, NetFactory.toExam(mMeetId, mModuleId));
        } else {
            exeNetworkReq(KSurvey, NetFactory.toSurvey(mMeetId, mModuleId));
        }

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
            Intro intro = r.getData();
            Intro.setIntro(intro);
            mPaper = intro.getEv(TIntro.paper);
            switch (id) {
                case KExam:
                    mTvTitle.setText(mPaper.getString(TPaper.name));
                    break;
                case KSurvey:
                    mTvTitle.setText(mPaper.getString(TPaper.paperName));
                    break;
            }
            List list = mPaper.getList(TPaper.questions);
            mTvCount.setText(list.size() + "道题");

            //获取起始结束时间
            mStartTime = Long.valueOf(intro.getString(TIntro.startTime));
            mEndTime = Long.valueOf(intro.getString(TIntro.endTime));
            mNowTime = Long.valueOf(intro.getString(TIntro.serverTime));
            canStart = mStartTime <= mNowTime && mNowTime < mEndTime;
            if (mStartTime > mNowTime) {//未开始的话开始计时
                Long maxCount = (mStartTime - mNowTime) / 60000;
                Flowable.interval(1, TimeUnit.MINUTES)
                        .take(maxCount + 1)
                        .subscribe(createSub());
            }
            //按格式显示起始结束时间
            StringBuilder time = null;
            String startDate = TimeUtil.formatMilli(mStartTime, TimeFormat.simple_ymd);
            String endDate = TimeUtil.formatMilli(mEndTime, TimeFormat.simple_ymd);
            if (startDate.equals(endDate)) {
                //同一天
                time = new StringBuilder(startDate)
                        .append(" ")
                        .append(TimeUtil.formatMilli(mStartTime, TimeFormat.from_h_to_m_24))
                        .append("~")
                        .append(TimeUtil.formatMilli(mEndTime, TimeFormat.from_h_to_m_24));
            } else {
                time = new StringBuilder()
                        .append(TimeUtil.formatMilli(mStartTime, TimeFormat.from_y_to_m_24))
                        .append("~")
                        .append(TimeUtil.formatMilli(mEndTime, TimeFormat.from_y_to_m_24));
            }
            mTvTime.setText(time);
        } else {
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
            case R.id.exam_intro_tv_start://开始考试
                if (canStart) {
                    TopicActivity.nav(IntroActivity.this, mModuleId);
                    finish();
                } else if (mStartTime > mNowTime) {
                    //TODO:考试未开始
                } else {
                    //TODO:考试结束
                }
                break;
            default:
                break;
        }

    }

    private DisposableSubscriber<Long> createSub() {
        mSub = new DisposableSubscriber<Long>() {

            @Override
            public void onNext(@NonNull Long aLong) {
                LogMgr.d(TAG, "createSub" + "考试未开始");
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
            }

            @Override
            public void onComplete() {
                canStart = true;
            }
        };
        return mSub;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSub != null && !mSub.isDisposed()) {
            mSub.dispose();
            mSub = null;
        }
    }

}

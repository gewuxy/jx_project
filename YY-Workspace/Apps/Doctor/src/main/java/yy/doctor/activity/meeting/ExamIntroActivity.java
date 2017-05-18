package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;
import lib.ys.LogMgr;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.exam.Intro;
import yy.doctor.model.exam.Intro.TIntro;
import yy.doctor.model.exam.Paper;
import yy.doctor.model.exam.Paper.TPaper;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 考试介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class ExamIntroActivity extends BaseIntroActivity {

    private Paper mPaper;//本次考试试题信息

    private long mStartTime;//考试开始时间
    private long mEndTime;//考试结束时间
    private long mNowTime;//服务器当前时间

    private DisposableSubscriber<Long> mSub;
    private boolean canStart;//是否能开始考试

    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, ExamIntroActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mMeetId = intent.getStringExtra(Extra.KMeetId);
        mModuleId = intent.getStringExtra(Extra.KModuleId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "考试", this);
    }

    @Override
    public void setViews() {
        super.setViews();
        exeNetworkReq(NetFactory.toExam(mMeetId, mModuleId));
    }

    @Override
    protected void success(Intro intro) {
        mPaper = intro.getEv(TIntro.paper);
        mTvTitle.setText(mPaper.getString(TPaper.name));
        List list = mPaper.getList(TPaper.questions);
        mTvCount.setText(list.size() + "道题");

        //获取起始结束时间
        mStartTime = Long.valueOf(intro.getString(Intro.TIntro.startTime));
        mEndTime = Long.valueOf(intro.getString(Intro.TIntro.endTime));
        mNowTime = Long.valueOf(intro.getString(Intro.TIntro.serverTime));
        canStart = mStartTime <= mNowTime && mNowTime < mEndTime;
        if (mStartTime > mNowTime) {//未开始的话开始计时
            Long maxCount = (mStartTime - mNowTime) / 60000;
            Flowable.interval(1, TimeUnit.MINUTES)
                    .take(maxCount + 1)
                    .subscribe(createSub());
        }

        mTvTime.setText(getTime(mStartTime, mEndTime));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_intro_tv_start://开始考试
                if (canStart) {
                    TopicActivity.nav(ExamIntroActivity.this, mModuleId);
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

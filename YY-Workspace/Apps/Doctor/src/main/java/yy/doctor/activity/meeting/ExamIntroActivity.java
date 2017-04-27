package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.widget.TextView;

import lib.ys.activity.ActivityEx;
import lib.ys.ex.NavBar;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 考试介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class ExamIntroActivity extends ActivityEx {

    private TextView mTvTitle;
    private TextView mTvHost;
    private TextView mTvCount;
    private TextView mTvTime;
    private TextView mTvStart;

    @Override
    public void initData() {

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
        mTvStart = findView(R.id.exam_intro_tv_start);
    }

    @Override
    public void setViews() {

    }
}

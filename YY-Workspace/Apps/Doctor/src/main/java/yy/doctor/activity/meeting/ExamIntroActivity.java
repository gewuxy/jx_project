package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.exam.ExamTopicFrag;
import yy.doctor.util.Util;

/**
 * 考试介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class ExamIntroActivity extends BaseActivity {

    public static void nav(Context context, String[] info) {
        Intent i = new Intent(context, ExamTopicFrag.class);
        i.putExtra(Extra.KData, info);
        LaunchUtil.startActivity(context, i);
    }

    private TextView mTvTitle;  //试卷名称
    private TextView mTvHost;   //主办方
    private TextView mTvCount;  //总题数
    private TextView mTvTime;   //考试时间

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
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.exam_intro_tv_start);
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

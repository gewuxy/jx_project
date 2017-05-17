package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import java.util.ArrayList;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseVPActivity;
import lib.yy.view.ExamCaseGridView;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.ExamCaseAdapter;
import yy.doctor.frag.exam.TopicFrag;
import yy.doctor.model.exam.Intro;
import yy.doctor.model.exam.Intro.TIntro;
import yy.doctor.model.exam.Paper;
import yy.doctor.model.exam.Paper.TPaper;
import yy.doctor.model.exam.Topic;
import yy.doctor.util.Util;

/**
 * 考试(问卷)题目界面
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicActivity extends BaseVPActivity {
    //TODO:动画

    private static final long KDuration = 300l;//动画时长

    private ArrayList<Topic> mAllTopics;

    private LinearLayout mLl;                   //考题情况
    private ExamCaseGridView mGv;               //考题情况列表
    private ExamCaseAdapter mExamCaseAdapter;   //考试情况的Adapter

    private Animation mEnter;//进入动画
    private Animation mLeave;//离开动画
    private boolean mHasAnimation;//有动画在执行
    private Paper mPaper;
    private String mModuleId;

    public static void nav(Context context, String moduleId) {
        Intent i = new Intent(context, TopicActivity.class);
        i.putExtra(Extra.KData, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {

        mHasAnimation = false;
        mEnter = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.0f);
        mLeave = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                1.0f);

        mModuleId = getIntent().getStringExtra(Extra.KData);
        mPaper = Intro.getIntro().getEv(TIntro.paper);
        mAllTopics = mPaper.getList(TPaper.questions);

        for (Topic topic : mAllTopics) {
            TopicFrag frag = new TopicFrag();
            frag.setTopic(topic);
            add(frag);
        }

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "考试", this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_topic;
    }

    @Override
    public void findViews() {
        super.findViews();

        mLl = findView(R.id.exam_topic_case_all_ll);
        mGv = findView(R.id.exam_topic_case_all_gv);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.topic_rl_case);
        mLl.setOnClickListener(this);
        gvSet();

        setAnimation(mEnter);
        setAnimation(mLeave);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topic_rl_case:
                examCaseVisibility(!(mLl.getVisibility() == View.VISIBLE));
                break;
            case R.id.exam_topic_case_all_ll:
                examCaseVisibility(false);
                break;
            default:
                break;
        }
    }

    /**
     * 设置GridView
     * 空白监听{@link ExamCaseGridView.OnInvalidListener}
     * Adapter{@link ExamCaseAdapter}
     */
    private void gvSet() {
        mExamCaseAdapter = new ExamCaseAdapter();
        mExamCaseAdapter.addAll(mAllTopics);

        mGv.setAdapter(mExamCaseAdapter);
        mGv.setOnItemClickListener((parent, view, position, id) -> examCaseVisibility(false));
        mGv.setOnInvalidListener(motionEvent -> {
            examCaseVisibility(false);
            return false;
        });
    }

    /**
     * 考题情况是否显示
     *
     * @param showState true显示,false不显示
     */
    private void examCaseVisibility(boolean showState) {
        //没有动画执行的时候
        if (!mHasAnimation) {
            mLl.setVisibility(showState ? View.VISIBLE : View.GONE);
            mLl.startAnimation(showState ? mEnter : mLeave);
        }
    }

    /**
     * Animation设置
     * 设置播放时长
     * 设置监听
     *
     * @param animation
     */
    private void setAnimation(Animation animation) {
        animation.setDuration(KDuration);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mHasAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHasAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.ys.model.MapList;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseVPActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.TopicCaseAdapter;
import yy.doctor.frag.exam.TopicFrag;
import yy.doctor.model.exam.Choose;
import yy.doctor.model.exam.Choose.TChoose;
import yy.doctor.model.exam.Intro;
import yy.doctor.model.exam.Intro.TIntro;
import yy.doctor.model.exam.Paper;
import yy.doctor.model.exam.Paper.TPaper;
import yy.doctor.model.exam.Topic;
import yy.doctor.model.exam.Topic.TTopic;

/**
 * 考试(问卷)题目界面
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public abstract class BaseTopicActivity extends BaseVPActivity {

    private static final long KDuration = 300l;//动画时长
    public static final int KVpSize = 3;

    private String mModuleId;
    protected Paper mPaper;//整套考题
    protected ArrayList<Topic> mAllTopics;
    private TopicCaseAdapter mTopicCaseAdapter;  //考题情况的Adapter

    private GridView mGv;           //考题情况列表
    private TextView mTvAll;        //总数
    private TextView mTvCount;      //已完成数
    private LinearLayout mLayout;   //考题情况

    private Animation mEnter;//进入动画
    private Animation mLeave;//离开动画
    private boolean mHasAnimation;//是否有动画在执行
    private boolean mTopicCaseShow;//是否在查看考题

    protected TextView mTvLeft;//NavBar左边的TextView
    private View mViewMid;//NavBar中边的View(显示考试情况的时候显示)
    private TextView mTvNavCount;//完成情况
    private TextView mTvNavAll;//总数

    protected boolean getTopicCaseShow() {
        return mTopicCaseShow;
    }

    @Override
    public void initData() {
        mHasAnimation = false;
        mTopicCaseShow = false;

        mModuleId = getIntent().getStringExtra(Extra.KData);
        mPaper = Intro.getIntro().getEv(TIntro.paper);
        mAllTopics = mPaper.getList(TPaper.questions);

        TopicFrag topicFrag = null;
        int all = mAllTopics.size();
        for (Topic topic : mAllTopics) {
            topicFrag = new TopicFrag();
            topicFrag.setTopic(topic);
            topicFrag.setOnNextListener(v -> {
                if (getCurrentItem() < all) {
                    getAnswer(mAllTopics);
                    String count = topic.getString(TTopic.id);
                    mTvAll.setText(count + "/" + all);
                    mTvNavAll.setText(count + "/" + all);
                    setCurrentItem(getCurrentItem() + 1);
                } else {
                    showToast("提交");
                }
            });
            add(topicFrag);
        }

    }

    @Override
    public void initNavBar(NavBar bar) {
        //添加左边text
        bar.addViewLeft(R.mipmap.nav_bar_ic_back, "", v -> {
            if (getTopicCaseShow()) {
                topicCaseVisibility(false);
            } else {
                finish();
            }
        });
        if (mTvLeft == null) {
            getTvLeft((ViewGroup) bar.getLayoutLeft());
        }
        //添加中间text
        //试题情况可见时显示
        mViewMid = inflate(R.layout.layout_exam_topic_nav_bar);
        bar.addViewMid(mViewMid);
        goneView(mViewMid);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_topic;
    }

    @Override
    public void findViews() {
        super.findViews();
        mLayout = findView(R.id.topic_case_all_layout);
        mGv = findView(R.id.topic_case_all_gv);
        mTvAll = findView(R.id.topic_tv_case_all);
        mTvCount = findView(R.id.topic_tv_case_finish_count);
        mTvNavCount = (TextView) mViewMid.findViewById(R.id.topic_tv_nar_bar_finish_count);
        mTvNavAll = (TextView) mViewMid.findViewById(R.id.topic_tv_nar_bar_all);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.topic_layout_case_all);
        setOnClickListener(R.id.topic_tv_nar_bar_all);
        setGv();

        setOffscreenPageLimit(KVpSize);

        //第一题
        String topicId = mAllTopics.get(0).getString(TTopic.id);
        mTvAll.setText(topicId + "/" + mAllTopics.size());
        mTvNavAll.setText(topicId + "/" + mAllTopics.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topic_layout_case_all:
                if (mEnter == null) {
                    mEnter = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,
                            0.0f,
                            Animation.RELATIVE_TO_SELF,
                            0.0f,
                            Animation.RELATIVE_TO_SELF,
                            1.0f,
                            Animation.RELATIVE_TO_SELF,
                            0.0f);
                    setAnimation(mEnter);
                }
                if (mLeave == null) {
                    mLeave = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,
                            0.0f,
                            Animation.RELATIVE_TO_SELF,
                            0.0f,
                            Animation.RELATIVE_TO_SELF,
                            0.0f,
                            Animation.RELATIVE_TO_SELF,
                            1.0f);
                    setAnimation(mLeave);
                }

                topicCaseVisibility(true);
                break;
            case R.id.topic_tv_nar_bar_all:
                topicCaseVisibility(false);
                break;
        }
    }

    /**
     * 获取试题答案
     *
     * @param topics
     */
    private MapList<String, String> getAnswer(List<Topic> topics) {
        MapList<String, String> answers = new MapList<>();//题号,答案
        int count = 0;
        for (Topic topic : topics) {
            List<Choose> chooses = topic.getList(TTopic.options);
            if (chooses == null) {
                chooses = topic.getList(TTopic.optionList);
            }
            //记录选中的选项
            StringBuffer answer = new StringBuffer();
            for (Choose choose : chooses) {
                if (choose.getBoolean(TChoose.check)) {
                    answer.append(choose.getString(TChoose.key));
                }
            }
            answers.add(topic.getString(TTopic.id), answer.toString());
            //标记是否已作答
            if (answer.toString().length() > 0) {
                topic.put(TTopic.finish, true);
                count++;
            } else {
                topic.put(TTopic.finish, false);
            }
        }

        mTvCount.setText(String.valueOf(count));
        mTvNavCount.setText("已完成" + count);
        return answers;
    }

    /**
     * 设置GridView
     * Adapter{@link TopicCaseAdapter}
     */
    private void setGv() {
        mTopicCaseAdapter = new TopicCaseAdapter();
        mTopicCaseAdapter.addAll(mAllTopics);

        mGv.setAdapter(mTopicCaseAdapter);
        mGv.setOnItemClickListener((parent, view, position, id) -> {
            setCurrentItem(position);
            String count = mAllTopics.get(position).getString(TTopic.id);
            mTvAll.setText(count + "/" + mAllTopics.size());
            mTvNavAll.setText(count + "/" + mAllTopics.size());
            topicCaseVisibility(false);
        });
    }

    /**
     * 考题情况是否显示
     *
     * @param showState true显示,false不显示
     */
    protected void topicCaseVisibility(boolean showState) {
        //没有动画执行的时候
        if (!mHasAnimation) {
            if (showState) {
                mTopicCaseAdapter.notifyDataSetChanged();
            }
            mTopicCaseShow = showState;
            mLayout.setVisibility(showState ? View.VISIBLE : View.GONE);
            mLayout.startAnimation(showState ? mEnter : mLeave);
            mViewMid.setVisibility(showState ? View.VISIBLE : View.GONE);
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

    /**
     * 获取左边文字
     *
     * @param layout
     */
    private void getTvLeft(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View childView = layout.getChildAt(i);
            if (childView instanceof ViewGroup) {
                getTvLeft((ViewGroup) childView);
            } else if (childView instanceof TextView) {
                mTvLeft = (TextView) childView;
            }
        }
    }
}
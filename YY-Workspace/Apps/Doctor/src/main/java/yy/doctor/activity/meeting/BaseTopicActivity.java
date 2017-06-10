package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseVPActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.TopicCaseAdapter;
import yy.doctor.frag.meeting.exam.TopicFrag;
import yy.doctor.model.meet.exam.Answer;
import yy.doctor.model.meet.exam.Answer.TAnswer;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Choice.TChoice;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.model.meet.exam.Paper;
import yy.doctor.model.meet.exam.Paper.TPaper;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * 考试(问卷)题目界面
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public abstract class BaseTopicActivity extends BaseVPActivity {

    private static final long KDuration = 300l; //动画时长
    public static final int KVpSize = 3;

    private TopicCaseAdapter mTopicCaseAdapter;  //考题情况的Adapter

    private GridView mGv;           //考题情况列表
    private TextView mTvCount;      //已完成数
    private LinearLayout mLayout;   //考题情况

    private Animation mEnter;       //进入动画
    private Animation mLeave;       //离开动画
    private boolean mIsAnimating;  //是否有动画在执行
    private boolean mTopicCaseShow; //是否在查看考题

    private View mViewMid;          //NavBar中边的View(显示考试情况的时候显示)
    private TextView mTvNavCount;   //完成情况

    protected int mCount; // 完成数量
    protected String mPaperId;
    protected String mMeetId;
    protected String mModuleId;
    protected Intro mIntro;
    protected Paper mPaper; // 整套考题
    protected List<Topic> mAllTopics;
    protected TextView mTvAll; // 总数(底部)
    protected TextView mTvNavAll; // 总数(NavBar)
    protected TextView mTvLeft; // NavBar左边的TextView

    protected boolean getTopicCaseShow() {
        return mTopicCaseShow;
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_topic;
    }

    @Override
    public void initData() {
        mIsAnimating = false;
        mTopicCaseShow = false;

        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
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

        setOffscreenPageLimit(KVpSize);

        setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvAll.setText(getProgress(position));
                mTvNavAll.setText(getProgress(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
    protected ArrayList<Answer> getAnswer(List<Topic> topics) {
        ArrayList<Answer> answers = new ArrayList<>();//题号,答案
        mCount = 0;
        List<Choice> choices;
        Answer answer;
        StringBuffer answerStr;
        for (Topic topic : topics) {
            choices = topic.getList(TTopic.options);

            answer = new Answer();
            answer.put(TAnswer.id, topic.getString(TTopic.id));
            //记录选中的选项
            answerStr = new StringBuffer();
            for (Choice choice : choices) {
                if (choice.getBoolean(TChoice.check)) {
                    answerStr.append(choice.getString(TChoice.key));
                }
            }
            answer.put(TAnswer.answer, answerStr.toString());
            answers.add(answer);

            //标记是否已作答
            if (answerStr.toString().length() > 0) {
                topic.put(TTopic.finish, true);
                mCount++;
            } else {
                topic.put(TTopic.finish, false);
            }
        }

        mTvCount.setText(String.valueOf(mCount));
        mTvNavCount.setText("已完成" + mCount);
        return answers;
    }

    /**
     * 加载TopicFrag
     */
    protected void initFrag() {
        mPaper = mIntro.getEv(TIntro.paper);
        mPaperId = mPaper.getString(TPaper.id);
        mAllTopics = mPaper.getList(TPaper.questions);

        TopicFrag topicFrag = null;
        int size = mAllTopics.size();

        for (int i = 0; i < size; i++) {
            topicFrag = new TopicFrag();
            Topic topic = mAllTopics.get(i);
            topicFrag.setTopic(topic);
            //最后一题
            if (i == mAllTopics.size() - 1) {
                topicFrag.isLast();
            }
            topicFrag.setOnNextListener(v -> {
                getAnswer(mAllTopics);
                if (getCurrentItem() < size - 1) {
                    setCurrentItem(getCurrentItem() + 1);
                } else {
                    trySubmit(size - mCount);
                }
            });
            add(topicFrag);
        }
    }

    /**
     * 设置GridView 和初始化
     * Adapter{@link TopicCaseAdapter}
     */
    protected void initFirstGv() {
        if (mAllTopics.size() > 0) {
            //第一题
            mTvAll.setText(getProgress(0));
            mTvNavAll.setText(getProgress(0));
        }

        mTopicCaseAdapter = new TopicCaseAdapter();
        mTopicCaseAdapter.addAll(mAllTopics);

        mGv.setAdapter(mTopicCaseAdapter);
        mGv.setOnItemClickListener((parent, view, position, id) -> {
            setCurrentItem(position);
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
        if (!mIsAnimating) {
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
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 获取当前题目占总题目的百分比
     *
     * @param position
     * @return
     */
    protected String getProgress(int position) {
        StringBuffer sb = new StringBuffer();
        if (mAllTopics != null && mAllTopics.size() > 0) {
            sb.append(mAllTopics.get(position).getString(TTopic.sort))
                    .append("/")
                    .append(mAllTopics.size());
        }
        return sb.toString();
    }

    /**
     * 提交答案
     */
    protected abstract void submit();

    /**
     * 按提交
     *
     * @param noFinish
     */
    protected abstract void trySubmit(int noFinish);
}
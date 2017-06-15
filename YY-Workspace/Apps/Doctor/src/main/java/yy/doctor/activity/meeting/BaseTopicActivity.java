package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
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
import yy.doctor.dialog.HintDialogMain;
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
    // FIXME: 2017/6/15 情况界面
    private static final int KDuration = 300; //动画时长
    private static final int KVpSize = 3;

    private TopicCaseAdapter mTopicCaseAdapter;  //考题情况的Adapter

    private GridView mGv;           //考题情况列表
    private TextView mTvFinish;      //已完成数
    private TextView mTvAllFinish; // 已完成数(查看考题)
    private LinearLayout mLayout;   //考题情况

    private Animation mEnter;       //进入动画
    private Animation mLeave;       //离开动画
    private boolean mIsAnimating;  //是否有动画在执行

    protected int mCount; // 完成数量
    protected String mPaperId;
    protected String mMeetId;
    protected String mModuleId;
    protected Intro mIntro;
    protected Paper mPaper; // 整套考题
    protected List<Topic> mAllTopics;
    protected TextView mTvCase; // 总数(底部)
    protected TextView mTvAllCase; // 总数(查看考题)
    private HintDialogMain mSubDialog;
    private HintDialogMain mExitDialog;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_topic;
    }

    @Override
    public void initData() {
        mIsAnimating = false;

        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.mipmap.nav_bar_ic_back, v -> exit());
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayout = findView(R.id.topic_case_all_layout_progress);
        mGv = findView(R.id.topic_case_all_gv);
        mTvCase = findView(R.id.topic_case_tv_all);
        mTvAllCase = findView(R.id.topic_case_all_tv_all);
        mTvFinish = findView(R.id.topic_case_tv_finish);
        mTvAllFinish = findView(R.id.topic_case_all_tv_finish);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.topic_case_layout_progress);
        setOnClickListener(R.id.topic_case_all_layout_all);
        setOnClickListener(R.id.topic_case_all_layout_progress);

        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);

        setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvCase.setText(getProgress(position));
                mTvAllCase.setText(getProgress(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
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
        switch (v.getId()) {
            case R.id.topic_case_layout_progress:
                topicCaseVisibility(true);
                break;
            case R.id.topic_case_all_layout_all:
                topicCaseVisibility(false);
                break;
            case R.id.topic_case_all_layout_progress:
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

        mTvFinish.setText(String.valueOf(mCount));
        mTvAllFinish.setText(String.valueOf(mCount));
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
            mTvCase.setText(getProgress(0));
            mTvAllCase.setText(getProgress(0));
        }

        mTopicCaseAdapter = new TopicCaseAdapter();
        mTopicCaseAdapter.addAll(mAllTopics);

        mGv.setAdapter(mTopicCaseAdapter);
        mGv.setOnItemClickListener((parent, view, position, id) -> {
            topicCaseVisibility(false);
            setCurrentItem(position);
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
     * 按提交
     *
     * @param noFinish
     */
    protected void trySubmit(int noFinish) {
        //考试时间未完
        mSubDialog = new HintDialogMain(BaseTopicActivity.this);
        mSubDialog.setHint(setDialogHint(noFinish));

        mSubDialog.addButton("确定", "#0682e6", v -> {
            submit();
            mSubDialog.dismiss();
        });
        mSubDialog.addButton("取消", "#666666", v -> mSubDialog.dismiss());
        mSubDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSubDialog != null) {
            if (mSubDialog.isShowing()) {
                mSubDialog.dismiss();
            }
            mSubDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    /**
     * 退出提示
     */
    private void exit() {
        mExitDialog = new HintDialogMain(BaseTopicActivity.this);
        mExitDialog.setHint("确定退出?");
        mExitDialog.addButton("确定", "#0682e6", v1 -> {
            finish();
            mExitDialog.dismiss();
        });
        mExitDialog.addButton("取消", "#666666", v1 -> mExitDialog.dismiss());
        mExitDialog.show();
    }

    /**
     * 考题情况是否显示
     *
     * @param showState true显示,false不显示
     */
    private void topicCaseVisibility(boolean showState) {
        //没有动画执行的时候
        if (!mIsAnimating) {
            if (showState) {
                mTopicCaseAdapter.notifyDataSetChanged();
            }
            mLayout.setVisibility(showState ? View.VISIBLE : View.GONE);
            mLayout.startAnimation(showState ? mEnter : mLeave);
        }
    }

    /**
     * Animation设置,设置播放时长,设置监听
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
     * 提交答案
     */
    protected abstract void submit();

    /**
     * 设置提交时Dialog的提示语
     *
     * @param noFinish
     * @return
     */
    protected abstract String setDialogHint(int noFinish);
}
package yy.doctor.ui.activity.meeting;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lib.ys.model.MapList;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseVPActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.TopicCaseAdapter;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.model.meet.exam.Answer;
import yy.doctor.model.meet.exam.Answer.TAnswer;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.model.meet.exam.Paper;
import yy.doctor.model.meet.exam.Paper.TPaper;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;
import yy.doctor.ui.frag.meeting.exam.TopicFrag;
import yy.doctor.ui.frag.meeting.exam.TopicFrag.OnTopicListener;
import yy.doctor.ui.frag.meeting.exam.TopicFragArg;

/**
 * 考试(问卷)题目界面
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public abstract class BaseTopicActivity extends BaseVPActivity implements OnTopicListener {

    private static final int KVpSize = 3;

    private final int KDuration = 300; // 动画时长

    private TopicCaseAdapter mTopicCaseAdapter; // 考题情况的Adapter

    private GridView mGv; // 考题情况列表
    private TextView mTvFinish; // 已完成数
    private TextView mTvAllFinish; // 已完成数(查看考题)
    private LinearLayout mLayout; // 考题情况
    private LinearLayout mLayoutBg; // 考题背景

    private Animation mEnter; // 进入动画
    private Animation mLeave; // 离开动画
    private Animation mBgShow; // 背景显示
    private Animation mBgHide; // 背景消失
    private boolean mIsAnimating; // 是否有动画在执行

    private HintDialogMain mSubDialog; // 提交提示
    private HintDialogMain mExitDialog; // 退出提示

    protected int mCount; // 完成数量
    protected String mMeetId;
    protected String mModuleId;
    protected Intro mIntro;
    protected Paper mPaper; // 整套考题
    protected List<Topic> mAllTopics; // 所有的考题
    protected MapList<Integer, Answer> mAnswers; // 答案
    protected TextView mTvCase; // 总数(底部)
    protected TextView mTvAllCase; // 总数(查看考题)

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_topic;
    }

    @Override
    public void initData() {
        mCount = 0;
        mIsAnimating = false;
        mAnswers = new MapList<>();
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.mipmap.nav_bar_ic_back, v -> exit());
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mLayout = findView(R.id.topic_case_all_layout_progress);
        mLayoutBg = findView(R.id.topic_case_all_background_layout_progress);
        mGv = findView(R.id.topic_case_all_gv);
        mTvCase = findView(R.id.topic_case_tv_all);
        mTvAllCase = findView(R.id.topic_case_all_tv_all);
        mTvFinish = findView(R.id.topic_case_tv_finish);
        mTvAllFinish = findView(R.id.topic_case_all_tv_finish);
    }

    @CallSuper
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
        if (mBgShow == null) {
            mBgShow = new AlphaAnimation(0.0f, 1.0f);
            setAnimation(mBgShow);
        }
        if (mBgHide == null) {
            mBgHide = new AlphaAnimation(1.0f, 0.0f);
            setAnimation(mBgHide);
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
     * 加载TopicFrag
     */
    protected void initFrag() {
        mPaper = mIntro.getEv(TIntro.paper);
        mAllTopics = mPaper.getList(TPaper.questions);

        TopicFrag topicFrag = null;
        int size = mAllTopics.size();
        for (int i = 0; i < size; i++) {
            //最后一题
            boolean isLast = false;
            if (i == size - 1) {
                isLast = true;
            }
            topicFrag = TopicFragArg.create(isLast, mAllTopics.get(i), i).build();
            topicFrag.setOnTopicListener(this);
            add(topicFrag);
        }
    }

    /**
     * 设置GridView(所有考题情况) 和初始化
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
            mLayoutBg.setVisibility(showState ? View.VISIBLE : View.GONE);
            mLayoutBg.startAnimation(showState ? mBgShow : mBgHide);
        }
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
            sb.append(position + 1)
                    .append("/")
                    .append(mAllTopics.size());
        }
        return sb.toString();
    }

    @Override
    public void topicFinish(int id, Answer answer) {
        Topic checkTopic = mAllTopics.get(id);
        if (answer.getString(TAnswer.answer).length() > 0) {
            // 选择了答案
            if (!checkTopic.getBoolean(TTopic.finish)) {
                // 之前未完成
                checkTopic.put(TTopic.finish, true);
                mCount++;
            }
        } else {
            // 没有选择答案
            if (checkTopic.getBoolean(TTopic.finish)) {
                checkTopic.put(TTopic.finish, false);
                mCount--;
            }
        }
        Answer a = mAnswers.getByKey(id);
        if (a == null) {
            mAnswers.add(Integer.valueOf(id), answer);
        } else {
            // 改答案
            a.put(answer);
        }
        mTvFinish.setText(String.valueOf(mCount));
        mTvAllFinish.setText(String.valueOf(mCount));
    }

    @Override
    public void toNext() {
        if (getCurrentItem() == mAllTopics.size() - 1) {
            toSubmit(mAllTopics.size() - mCount);
        } else {
            setCurrentItem(getCurrentItem() + 1);
        }
    }

    /**
     * 点击提交
     *
     * @param noFinish
     */
    protected void toSubmit(int noFinish) {
        // 未答完
        mSubDialog = new HintDialogMain(BaseTopicActivity.this);
        mSubDialog.setHint(submitHint(noFinish));
        mSubDialog.addButton(R.string.confirm, v -> {
            mSubDialog.dismiss();
            submit();
        });
        mSubDialog.addButton(R.string.cancel, R.color.text_666, v -> mSubDialog.dismiss());
        mSubDialog.show();
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
        mExitDialog.addButton(R.string.confirm, v -> {
            // 退出考试/问卷
            notify(NotifyType.study_end);
            finish();
            mExitDialog.dismiss();
        });
        mExitDialog.addButton(R.string.cancel, R.color.text_666, v -> mExitDialog.dismiss());
        mExitDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSubDialog != null) {
            mSubDialog.dismiss();
        }
        if (mExitDialog != null) {
            mExitDialog.dismiss();
        }
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
    protected abstract String submitHint(int noFinish);

}
package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.ys.model.MapList;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseVPActivity;
import lib.yy.view.TopicCaseGridView;
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

public class TopicActivity extends BaseVPActivity {

    private static final long KDuration = 300l;//动画时长

    private ArrayList<Topic> mAllTopics;

    private LinearLayout mLl;                    //考题情况
    private TopicCaseGridView mGv;               //考题情况列表
    private TopicCaseAdapter mTopicCaseAdapter;  //考题情况的Adapter
    private TextView mTvAll;        //总数
    private TextView mTvCount;      //已完成数

    private Animation mEnter;//进入动画
    private Animation mLeave;//离开动画
    private boolean mHasAnimation;//是否有动画在执行
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

        // TODO: 2017/5/18  frag的复用
        for (Topic topic : mAllTopics) {
            TopicFrag frag = new TopicFrag();
            frag.setTopic(topic);
            frag.setOnNextListener(v -> {
                if (getCurrentItem() < mAllTopics.size()) {
                    getAnswer(mAllTopics);
                    setCurrentItem(getCurrentItem() + 1);
                } else {
                    showToast("提交");
                }
            });
            add(frag);
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
        int all = topics.size();
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
        mTvAll.setText(count + "/" + all);
        return answers;
    }

    @Override
    public void initNavBar(NavBar bar) {
        /*bar.addViewLeft(R.mipmap.nav_bar_ic_back, text, new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });*/
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_topic;
    }

    @Override
    public void findViews() {
        super.findViews();

        mLl = findView(R.id.topic_case_all_ll);
        mGv = findView(R.id.topic_case_all_gv);
        mTvAll = findView(R.id.topic_tv_case_all);
        mTvCount = findView(R.id.topic_tv_case_finish_count);

    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.topic_rl_case);
        mLl.setOnClickListener(this);
        gvSet();

        setAnimation(mEnter);
        setAnimation(mLeave);
        mTvAll.setText("0/" + mAllTopics.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topic_rl_case:
                topicCaseVisibility(!(mLl.getVisibility() == View.VISIBLE));
                break;
            case R.id.topic_case_all_ll:
                topicCaseVisibility(false);
                break;
            default:
                break;
        }
    }

    /**
     * 设置GridView
     * 空白监听{@link TopicCaseGridView.OnInvalidListener}
     * Adapter{@link TopicCaseAdapter}
     */
    private void gvSet() {
        mTopicCaseAdapter = new TopicCaseAdapter();
        mTopicCaseAdapter.addAll(mAllTopics);

        mGv.setAdapter(mTopicCaseAdapter);
        mGv.setOnItemClickListener((parent, view, position, id) -> {
            topicCaseVisibility(false);
            setCurrentItem(position);
        });
        mGv.setOnInvalidListener(motionEvent -> {
            topicCaseVisibility(false);
            return false;
        });
    }

    /**
     * 考题情况是否显示
     *
     * @param showState true显示,false不显示
     */
    private void topicCaseVisibility(boolean showState) {
        //没有动画执行的时候
        if (!mHasAnimation) {
            if (showState) {
                mTopicCaseAdapter.notifyDataSetChanged();
            }
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
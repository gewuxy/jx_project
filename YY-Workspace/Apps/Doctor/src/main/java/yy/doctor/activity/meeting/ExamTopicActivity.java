package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import java.util.ArrayList;

import lib.ys.LogMgr;
import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseVPActivity;
import lib.yy.view.ExamCaseGridView;
import lib.yy.view.ExamCaseGridView.OnInvalidListener;
import yy.doctor.R;
import yy.doctor.adapter.ExamCaseAdapter;
import yy.doctor.frag.exam.ExamTopicFrag;
import yy.doctor.frag.exam.ExamTopicFrag.OnNextListener;
import yy.doctor.model.exam.ExamTopic;
import yy.doctor.util.Util;

import static yy.doctor.model.exam.ExamTopic.TExamTopic.choose;
import static yy.doctor.model.exam.ExamTopic.TExamTopic.question;

/**
 * 考试题目界面
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicActivity extends BaseVPActivity {

    private ArrayList<ExamTopic> mAllTopics;    //考题的数据,服务器返回的json字符串

    private boolean mIsShowed = false;          //考题情况是否显示
    private LinearLayout mLl;                   //考题情况
    private ExamCaseGridView mGv;               //考题情况列表
    private ExamCaseAdapter mExamCaseAdapter;   //考试情况的Adapter

    @Override
    public void initData() {
        mAllTopics = new ArrayList<>();

        for (int i = 0; i < 3; i++) {

            ArrayList<String> chooses = new ArrayList<>(); //考题的选项
            for (int j = 0; j < 3; j++) {
                chooses.add("放假" + (i * j));
            }
            ExamTopic examTopic = new ExamTopic();
            examTopic.put(question, "五一放假");
            examTopic.put(choose, chooses);

            ExamTopicFrag frag = new ExamTopicFrag();
            frag.setExamTopic(examTopic);
            frag.setData(chooses);
            frag.setOnNextListener(new OnNextListener() {
                @Override
                public void onNext(View v) {
                    int next = getCurrentItem() + 1;
                    if (next >= 3)
                        setCurrentItem(0);
                    else
                        setCurrentItem(next);
                    mExamCaseAdapter.notifyDataSetChanged();
                }
            });
            mAllTopics.add(examTopic);
            add(frag);
        }
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_exam_topic;
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

        setOnClickListener(R.id.exam_topic_rl_case);
        mLl.setOnClickListener(this);
        gvSet();
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "考试", this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_topic_rl_case:
                examCaseVisibility(!mIsShowed);
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
        mGv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogMgr.e(TAG, "item" + position);
                examCaseVisibility(false);
            }
        });
        mGv.setOnInvalidListener(new OnInvalidListener() {
            @Override
            public boolean onInvalidPosition(int motionEvent) {
                examCaseVisibility(false);
                return false;
            }
        });
    }

    /**
     * 考题情况是否显示
     *
     * @param showState true显示,false不显示
     */
    private void examCaseVisibility(boolean showState) {
        mLl.setVisibility(showState ? View.VISIBLE : View.GONE);
        mIsShowed = showState;
    }
}
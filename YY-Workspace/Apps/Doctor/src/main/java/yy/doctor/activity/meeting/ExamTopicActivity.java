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
import yy.doctor.model.exam.ExamTopic;
import yy.doctor.model.exam.GroupExamTopic;
import yy.doctor.util.Util;

import static yy.doctor.model.exam.ExamTopic.TExamTopic.answer;


/**
 * 考试题目界面
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicActivity extends BaseVPActivity {

    private boolean mIsShowed = false;      //考题情况是否显示
    private ArrayList<GroupExamTopic> mList;//考题的数据
    private LinearLayout mLl;               //考题情况
    private ExamCaseGridView mGv;           //考题情况列表

    @Override
    public void initData() {
        mList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            GroupExamTopic groupExamTopic = new GroupExamTopic();
            groupExamTopic.setQuestion("五一放假");
            for (int j = 0; j < 5; j++) {
                ExamTopic examTopic = new ExamTopic();
                examTopic.put(answer, "放假" + j);
                groupExamTopic.add(examTopic);
            }
            /*ExamTopic frag = new ExamTopic();
            frag.setData(answers);
            frag.setOnNextListener(new OnNextListener() {
                @Override
                public void onNext(View v) {
                    int i1 = getCurrentItem() + 1;
                    setCurrentItem(i1);
                }
            });
            add(frag);*/
            mList.add(groupExamTopic);
            ExamTopicFrag frag = new ExamTopicFrag();
            frag.setData(mList);
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

        final ExamCaseAdapter adapter = new ExamCaseAdapter();
        adapter.addAll(mList);
        mGv.setAdapter(adapter);
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

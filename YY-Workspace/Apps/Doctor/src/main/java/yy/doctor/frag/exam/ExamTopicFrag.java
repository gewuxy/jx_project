package yy.doctor.frag.exam;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

import lib.ys.LogMgr;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.yy.frag.base.BaseListFrag;
import yy.doctor.R;
import yy.doctor.adapter.ExamTopicAdapter;
import yy.doctor.model.exam.ExamTopic;

import static yy.doctor.model.exam.ExamTopic.TExamTopic.answer;
import static yy.doctor.model.exam.ExamTopic.TExamTopic.finish;
import static yy.doctor.model.exam.ExamTopic.TExamTopic.question;

/**
 * 单个考题
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicFrag extends BaseListFrag<String> {

    public interface OnNextListener {
        void onNext(View v);
    }

    private TextView mTvQ;
    private TextView mTvBtn;
    private OnNextListener mOnNextListener;
    private ExamTopic mExamTopic;                   //该题目的信息
    private ExamTopicAdapter mExamTopicAdapter;

    public void setOnNextListener(OnNextListener onNextListener) {
        mOnNextListener = onNextListener;
    }

    public void setExamTopic(ExamTopic examTopic) {
        mExamTopic = examTopic;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void findViews() {
        super.findViews();
        mTvQ = findView(R.id.exam_topic_tv_question);
        mTvBtn = findView(R.id.exam_topic_footer_tv_btn);
    }

    @Override
    public void setViews() {
        super.setViews();
        setDividerHeight(0);
        if (mTvQ != null && mExamTopic != null) {
            mTvQ.setText(mExamTopic.getString(question));
        }
        if (mTvBtn != null) {
            mTvBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Integer> answers = mExamTopicAdapter.getAnswer();//选择的答案
                    mExamTopic.put(answer, answers);
                    //题目是否已作答过
                    if (answers.size() > 0) {
                        mExamTopic.put(finish, true);
                    } else {
                        mExamTopic.put(finish, false);
                    }
                    LogMgr.e(TAG, answers.toString());
                    if (mOnNextListener != null) {
                        mOnNextListener.onNext(v);
                    }
                }
            });
        }
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_exam_topic_header);
    }

    @Override
    public View createFooterView() {
        return inflate(R.layout.layout_exam_topic_footer);
    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        mExamTopicAdapter = new ExamTopicAdapter();
        return mExamTopicAdapter;
    }

}

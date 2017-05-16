package yy.doctor.frag.exam;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.ys.LogMgr;
import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseListFrag;
import yy.doctor.R;
import yy.doctor.adapter.ExamTopicAdapter;
import yy.doctor.model.exam.Topic;

import static yy.doctor.model.exam.Topic.TTopic.answer;
import static yy.doctor.model.exam.Topic.TTopic.finish;
import static yy.doctor.model.exam.Topic.TTopic.title;

/**
 * 单个考题
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicFrag extends BaseListFrag<String, ExamTopicAdapter> {

    public interface OnNextListener {
        void onNext(View v);
    }

    private TextView mTvQ;
    private TextView mTvBtn;
    private OnNextListener mOnNextListener;
    private Topic mTopic;                   //该题目的信息

    public void setOnNextListener(OnNextListener onNextListener) {
        mOnNextListener = onNextListener;
    }

    public void setTopic(Topic topic) {
        mTopic = topic;
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
        if (mTvQ != null && mTopic != null) {
            mTvQ.setText(mTopic.getString(title));
        }

        if (mTvBtn != null) {
            mTvBtn.setOnClickListener(v -> {
                ExamTopicAdapter adapter = (ExamTopicAdapter) getAdapter();
                List<Integer> answers = adapter.getAnswers();//选择的答案
                mTopic.put(answer, answers);
                //题目是否已作答过
                if (answers.size() > 0) {
                    mTopic.put(finish, true);
                } else {
                    mTopic.put(finish, false);
                }

                LogMgr.d(TAG, answers.toString());

                if (mOnNextListener != null) {
                    mOnNextListener.onNext(v);
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

}

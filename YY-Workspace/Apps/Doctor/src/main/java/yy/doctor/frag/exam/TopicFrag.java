package yy.doctor.frag.exam;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.ys.LogMgr;
import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseListFrag;
import yy.doctor.R;
import yy.doctor.adapter.TopicAdapter;
import yy.doctor.model.exam.Choose;
import yy.doctor.model.exam.Topic;
import yy.doctor.model.exam.Topic.TTopic;

/**
 * 单个考题/问卷
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicFrag extends BaseListFrag<Choose, TopicAdapter> {

    private TextView mTvQ;
    private TextView mTvBtn;
    private OnNextListener mOnNextListener;
    private Topic mTopic;                   //该题目的信息
    private TopicAdapter mAdapter;
    private List<Choose> mChooses;

    public interface OnNextListener {
        void onNext(View v);
    }

    public void setOnNextListener(OnNextListener onNextListener) {
        mOnNextListener = onNextListener;
    }

    /**
     * 设置单个考题
     * @param topic
     */
    public void setTopic(Topic topic) {
        mTopic = topic;
        //设置选项
        mChooses = topic.getList(TTopic.options);
        if (mChooses == null) {
            mChooses = topic.getList(TTopic.optionList);
        }
        setData(mChooses);
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
        getLv().setBackgroundResource(R.color.white);

        mTvQ.setText(mTopic.getString(TTopic.id) + ". " + mTopic.getString(TTopic.title));
        //单选隐藏下一题的按钮
        if (mTopic.getInt(TTopic.qtype) == 0) {
            mTvBtn.setVisibility(View.GONE);
        } else {
            //TODO:单双选
            //设置多选
//            mAdapter.setNoSingle();
        }
        mTvBtn.setOnClickListener(v -> {
            /*List<String> answers = mAdapter.getAnswers();//选择的答案
            mTopic.put(TTopic.answer, answers);
            //题目是否已作答过
            if (answers.size() > 0) {
                mTopic.put(TTopic.finish, true);
            } else {
                mTopic.put(TTopic.finish, false);
            }

            LogMgr.d(TAG, answers.toString());*/

            if (mOnNextListener != null) {
                mOnNextListener.onNext(v);
            }
        });
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

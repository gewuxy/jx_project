package yy.doctor.frag.meeting.exam;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseListFrag;
import yy.doctor.R;
import yy.doctor.adapter.meeting.TopicAdapter;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * 单个考题/问卷
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicFrag extends BaseListFrag<Choice, TopicAdapter> {

    private TextView mTvQ;
    private TextView mTvBtn;
    private Topic mTopic; // 该题目的信息
    private OnTopicListener mOnTopicListener;
    private boolean isLast; // 最后一题

    public interface OnTopicListener {
        void onNext();
        void onSubmit();
    }

    public void setOnTopicListener(OnTopicListener onTopicListener) {
        mOnTopicListener = onTopicListener;
    }

    public void isLast() {
        isLast = true;
    }

    /**
     * 设置单个考题
     *
     * @param topic
     */
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
        setBackgroundResource(R.color.white);

        //设置题目
        StringBuffer title = new StringBuffer(mTopic.getString(TTopic.sort));
        //设置选项
        setData(mTopic.getList(TTopic.options));

        if (mTopic.getInt(TTopic.qtype) == 0) {
            //单选隐藏下一题的按钮
            mTvBtn.setVisibility(View.GONE);
            getAdapter().setIsSingle(true);
            getAdapter().setOnItemCheckListener(v -> toNext());
            title.append(".(单选)");
        } else {
            // 设置多选
            getAdapter().setIsSingle(false);
            getAdapter().setOnItemCheckListener(v -> mTopic.put(TTopic.finish, v.isSelected()));
            mTvBtn.setOnClickListener(v -> toNext());
            title.append(".(多选)");
        }
        mTvQ.setText(title.append(mTopic.getString(TTopic.title)).toString());

        // 最后一题的时候
        if (isLast) {
            mTvBtn.setText("提交");
            mTvBtn.setVisibility(View.VISIBLE);
            mTvBtn.setOnClickListener(v -> {
                if (mOnTopicListener != null) {
                    mOnTopicListener.onSubmit();
                }
            });
        }
    }

    private void toNext() {
        if (mOnTopicListener != null) {
            mOnTopicListener.onNext();
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

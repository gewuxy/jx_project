package yy.doctor.ui.frag.meeting.exam;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import io.reactivex.Flowable;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseListFrag;
import yy.doctor.R;
import yy.doctor.adapter.meeting.TopicAdapter;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Choice.TChoice;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;
import yy.doctor.model.meet.exam.Topic.TopicType;

/**
 * 单个 考题 / 问卷
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */
@Route
public class TopicFrag extends BaseListFrag<Choice, TopicAdapter> {

    private TextView mTvTitle; // 题目
    private TextView mTvNext; // 下一题(提交)按钮

    private int mLastPosition;
    private OnTopicListener mOnTopicListener;

    @Arg(optional = true)
    int mListId; // 题号
    @Arg(optional = true)
    boolean mLast; // 最后一题
    @Arg(optional = true)
    Topic mTopic; // 该题目的信息

    public interface OnTopicListener {
        /**
         * @param listId  题号
         * @param titleId 题目Id
         * @param answer  该题选择的答案
         */
        void topicFinish(int listId, int titleId, String answer);

        void toNext();
    }

    public void setOnTopicListener(OnTopicListener onTopicListener) {
        mOnTopicListener = onTopicListener;
    }

    @Override
    public void initData() {
        mLastPosition = ConstantsEx.KInvalidValue;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTitle = findView(R.id.exam_topic_tv_question);
        mTvNext = findView(R.id.exam_topic_footer_tv_btn);
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(0);
        setBackgroundResource(R.color.white);

        // 拼接题目
        StringBuffer title = new StringBuffer().append(mListId + 1);
        @TopicType int type = mTopic.getInt(TTopic.qtype);
        if (type == TopicType.choice_single) {
            // 单选, 隐藏下一题的按钮
            goneView(mTvNext);
            title.append(".(单选)");
        } else {
            // 多选, 显示下一题的按钮
            showView(mTvNext);
            title.append(".(多选)");
        }
        title.append(mTopic.getString(TTopic.title));

        mTvTitle.setText(title); // 设置题目
        setData(mTopic.getList(TTopic.options)); // 设置选项

        // 最后一题的时候
        if (mLast) {
            showView(mTvNext);
            mTvNext.setText(R.string.submit);
        }
        setOnClickListener(mTvNext);
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
    public void onClick(View v) {
        if (mOnTopicListener != null) {
            mOnTopicListener.toNext();
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        Choice item = getItem(position);
        String answer = ConstantsEx.KEmptyValue;
        ImageView ivAnswer = getAdapter().getCacheVH(position).getIvAnswer();

        @TopicType int type = mTopic.getInt(TTopic.qtype);
        switch (type) {
            case TopicType.choice_single: {
                // 单选
                if (mLastPosition != position) {
                    if (mLastPosition != ConstantsEx.KInvalidValue) {
                        // 取消之前的选择
                        getAdapter().getCacheVH(mLastPosition).getIvAnswer().setSelected(false);
                        getItem(mLastPosition).put(TChoice.check, false);
                    }
                    // 选择其他选项
                    ivAnswer.setSelected(true);
                    item.put(TChoice.check, true);
                    mLastPosition = position;
                }
                answer = item.getString(TChoice.key);
                if (!mLast) {
                    onClick(mTvNext);
                }
            }
            break;
            case TopicType.choice_multiple: {
                // 多选
                boolean selected = !ivAnswer.isSelected();
                ivAnswer.setSelected(selected);
                item.put(TChoice.check, selected);

                StringBuffer sb = new StringBuffer();
                Flowable.fromIterable(getData())
                        .filter(choice -> choice.getBoolean(TChoice.check))
                        .subscribe(choice -> sb.append(choice.getString(TChoice.key)));
                answer = sb.toString();
            }
            break;
        }

        if (mOnTopicListener != null) {
            YSLog.d(TAG, "onItemClick:" + answer);
            mOnTopicListener.topicFinish(mListId, mTopic.getInt(TTopic.id), answer);
        }
    }

}

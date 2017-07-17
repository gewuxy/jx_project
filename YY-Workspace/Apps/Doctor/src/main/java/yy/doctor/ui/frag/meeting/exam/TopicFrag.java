package yy.doctor.ui.frag.meeting.exam;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseListFrag;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.TopicAdapter;
import yy.doctor.model.meet.exam.Answer;
import yy.doctor.model.meet.exam.Answer.TAnswer;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Choice.TChoice;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TopicType;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * 单个 考题 / 问卷
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public class TopicFrag extends BaseListFrag<Choice, TopicAdapter> {

    private TextView mTvQ; // 题目
    private TextView mTvBtn; // 下一题(提交)按钮
    private OnTopicListener mOnTopicListener;
    private Answer mAnswer;
    private String mOptions; // 记录答案

    private boolean mLast; // 最后一题
    private Topic mTopic; // 该题目的信息
    private int mTitleId; // 题号

    public interface OnTopicListener {
        void topicFinish(int id, Answer answer);
        void toNext();
    }

    public void setOnTopicListener(OnTopicListener onTopicListener) {
        mOnTopicListener = onTopicListener;
    }

    @Override
    public void initData() {
        Bundle b = getArguments();
        if (b != null) {
            mTopic = (Topic) b.getSerializable(Extra.KData);
            mLast = b.getBoolean(Extra.KType, false);
            mTitleId = b.getInt(Extra.KId);
        }
        mOptions = new String();
        mAnswer = new Answer();
        // 答案题号
        mAnswer.put(TAnswer.id, mTopic.getString(TTopic.id));
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

        // 设置题目
        StringBuffer title = new StringBuffer().append(mTitleId + 1);
        // 设置选项
        setData(mTopic.getList(TTopic.options));

        if (mTopic.getInt(TTopic.qtype) == TopicType.choice_single) {
            // 单选, 隐藏下一题的按钮
            goneView(mTvBtn);
            getAdapter().setSingle(true);
            getAdapter().setOnChoiceListener((option, selectState) -> {
                if (mOptions.equals(option)) {
                    return;
                }
                mOptions = option;
                onSelect(mTitleId, mOptions);
                if (!mLast) {
                    toNext();
                }
            });
            title.append(".(单选)");
        } else {
            // 多选, (默认显示)下一题的按钮
            getAdapter().setSingle(false);
            getAdapter().setOnChoiceListener((option, selectState) -> {
                // FIXME: 不用for
                List<Choice> choices = getData();
                for (Choice choice : choices) {
                    String key = choice.getString(TChoice.key);
                    if (choice.getBoolean(TChoice.check)) {
                        // 选中
                        if (!mOptions.contains(key)) {
                            // 答案没有
                            mOptions = mOptions.concat(key);
                        }
                    } else {
                        // 取消选择
                        if (mOptions.contains(key)) {
                            // 答案已有
                            mOptions = mOptions.replaceAll(key, "");
                        }
                    }
                }
                onSelect(mTitleId, mOptions);
            });
            title.append(".(多选)");
        }
        mTvQ.setText(title.append(mTopic.getString(TTopic.title)).toString());

        // 最后一题的时候
        if (mLast) {
            showView(mTvBtn);
            mTvBtn.setText(R.string.submit);
        }
        mTvBtn.setOnClickListener(v -> toNext());
    }

    private void onSelect(int id, String mOptions) {
        mAnswer.put(TAnswer.answer, mOptions);
        if (mOnTopicListener != null) {
            mOnTopicListener.topicFinish(id, mAnswer);
        }
    }

    private void toNext() {
        if (mOnTopicListener != null) {
            mOnTopicListener.toNext();
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

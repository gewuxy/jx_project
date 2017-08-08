package yy.doctor.adapter.meeting;

import android.widget.ImageView;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.TopicVH;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Choice.TChoice;

/**
 * 考试/问卷 题目Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicAdapter extends AdapterEx<Choice, TopicVH> {

    private OnChoiceListener mOnChoiceListener;
    private boolean mIsSingle; // 是否单选
    private TopicVH lastTopicVH;
    private int lastPosition;

    public interface OnChoiceListener {
        void onItemCheckListener(String option, boolean selectState);
    }

    public void setOnChoiceListener(OnChoiceListener onChoiceListener) {
        mOnChoiceListener = onChoiceListener;
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_item;
    }

    @Override
    protected void refreshView(final int position, TopicVH holder) {
        Choice item = getItem(position);

        String option = item.getString(TChoice.key); // 选项
        holder.getTvAnswer().setText(option + ". " + item.getString(TChoice.value));
        ImageView ivAnswer = holder.getIvAnswer();
        ivAnswer.setSelected(item.getBoolean(TChoice.check));
        holder.getLayoutAnswer().setOnClickListener(v -> {
            if (mIsSingle) {
                // 单选
                if (lastTopicVH != null) {
                    lastTopicVH.getIvAnswer().setSelected(false);
                    getItem(lastPosition).put(TChoice.check, false);
                }
                lastTopicVH = holder;
                lastPosition = position;
                ivAnswer.setSelected(true);
                item.put(TChoice.check, true);
            } else {
                // 多选
                boolean selected = !ivAnswer.isSelected();
                ivAnswer.setSelected(selected);
                item.put(TChoice.check, selected);
            }

            if (mOnChoiceListener != null) {
                mOnChoiceListener.onItemCheckListener(option, item.getBoolean(TChoice.check));
            }
        });
    }

    public void setSingle(boolean isSingle) {
        mIsSingle = isSingle;
        notifyDataSetChanged();
    }
}

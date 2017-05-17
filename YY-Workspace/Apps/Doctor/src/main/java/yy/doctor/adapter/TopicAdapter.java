package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.ExamTopicVH;
import yy.doctor.model.exam.Choose;
import yy.doctor.model.exam.Choose.TChoose;

/**
 * 考试题目Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicAdapter extends AdapterEx<Choose, ExamTopicVH> {

    private OnItemCheckListener mOnItemCheckListener;

    public interface OnItemCheckListener {
        void onItemCheckListener(boolean isChecked, String key);
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        mOnItemCheckListener = onItemCheckListener;
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_item;
    }

    @Override
    protected void refreshView(final int position, ExamTopicVH holder) {
        holder.getTvAnswer().setText(getItem(position).getString(TChoose.key) + ". " + getItem(position).getString(TChoose.value));
        holder.getCbAnswer().setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mOnItemCheckListener != null) {
                String key = getItem(position).getString(TChoose.key);
                mOnItemCheckListener.onItemCheckListener(isChecked, key);
            }
        });
    }
}

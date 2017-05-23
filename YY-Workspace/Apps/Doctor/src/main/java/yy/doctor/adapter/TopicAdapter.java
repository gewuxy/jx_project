package yy.doctor.adapter;

import android.view.View;
import android.widget.ImageView;

import lib.ys.LogMgr;
import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.TopicVH;
import yy.doctor.model.exam.Choose;
import yy.doctor.model.exam.Choose.TChoose;

/**
 * 考试题目Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicAdapter extends AdapterEx<Choose, TopicVH> {

    private OnItemCheckListener mOnItemCheckListener;
    private boolean mIsSingle;//是否单选
    private TopicVH lastTopicVH;
    private int lastPosition;

    public interface OnItemCheckListener {
        void onItemCheckListener(View v);
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        mOnItemCheckListener = onItemCheckListener;
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_item;
    }


    @Override
    protected void refreshView(final int position, TopicVH holder) {
        Choose item = getItem(position);

        holder.getTvAnswer().setText(item.getString(TChoose.key) + ". " + item.getString(TChoose.value));
        ImageView ivAnswer = holder.getIvAnswer();
        ivAnswer.setSelected(item.getBoolean(TChoose.check));
        holder.getLayoutAnswer().setOnClickListener(v -> {
            if (mIsSingle) {//单选
                if (lastTopicVH != null) {
                    lastTopicVH.getIvAnswer().setSelected(false);
                    getItem(lastPosition).put(TChoose.check, false);
                }
                lastTopicVH = holder;
                lastPosition = position;
                ivAnswer.setSelected(true);
                item.put(TChoose.check, true);
            } else {//多选
                boolean selected = !ivAnswer.isSelected();
                ivAnswer.setSelected(selected);
                item.put(TChoose.check, selected);
            }

            if (mOnItemCheckListener != null) {
                LogMgr.d(TAG, "Checked");
                mOnItemCheckListener.onItemCheckListener(ivAnswer);
            }
        });
    }

    public void setIsSingle(boolean isSingle) {
        mIsSingle = isSingle;
        notifyDataSetChanged();
    }
}

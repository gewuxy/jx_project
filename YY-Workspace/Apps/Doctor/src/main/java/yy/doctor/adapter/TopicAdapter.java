package yy.doctor.adapter;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

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
    private List<TopicVH> mTopicVHs;

    public interface OnItemCheckListener {
        void onItemCheckListener(View v);
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        mOnItemCheckListener = onItemCheckListener;
    }

    @Override
    protected void initView(int position, TopicVH holder) {
        if (mTopicVHs == null) {
            mTopicVHs = new ArrayList<>();
        }
        mTopicVHs.add(holder);
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_item;
    }


    @Override
    protected void refreshView(final int position, TopicVH holder) {
        Choose item = getItem(position);

        holder.getTvAnswer().setText(item.getString(TChoose.key) + ". " + item.getString(TChoose.value));
        boolean history = item.getBoolean(TChoose.check);
        holder.getCbAnswer().setChecked(history);

        holder.getCbAnswer().setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mIsSingle) {//单选
                if (isChecked) {//选择
                    for (int i = 0; i < mTopicVHs.size(); i++) {
                        if (i != position) {
                            mTopicVHs.get(i).getCbAnswer().setChecked(false);
                            getItem(i).put(TChoose.check, false);
                        }
                    }
                }
            }
            item.put(TChoose.check, isChecked);
            if (mOnItemCheckListener != null) {
                LogMgr.d(TAG, "Checked");
                mOnItemCheckListener.onItemCheckListener(holder.getCbAnswer());
            }
        });
    }

    public void setIsSingle(boolean isSingle) {
        mIsSingle = isSingle;
        notifyDataSetChanged();
    }
}

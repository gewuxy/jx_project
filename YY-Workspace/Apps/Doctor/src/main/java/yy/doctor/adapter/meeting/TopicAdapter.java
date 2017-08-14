package yy.doctor.adapter.meeting;

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

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_item;
    }

    @Override
    protected void refreshView(final int position, TopicVH holder) {
        Choice item = getItem(position);

        holder.getTvAnswer().setText(item.getString(TChoice.key) + ". " + item.getString(TChoice.value)); // 选项
        holder.getIvAnswer().setSelected(item.getBoolean(TChoice.check));
    }

}

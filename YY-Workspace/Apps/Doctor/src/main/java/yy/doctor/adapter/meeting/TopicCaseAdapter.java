package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.TopicCaseVH;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * 考试/问卷 题目情况Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicCaseAdapter extends AdapterEx<Topic, TopicCaseVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_case_item;
    }

    @Override
    protected void refreshView(int position, TopicCaseVH holder) {
        Topic item = getItem(position);
        holder.getText().setText(item.getString(TTopic.sort));
        holder.getText().setSelected(item.getBoolean(TTopic.finish));
    }

}

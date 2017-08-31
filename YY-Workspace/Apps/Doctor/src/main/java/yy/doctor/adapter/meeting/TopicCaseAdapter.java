package yy.doctor.adapter.meeting;

import android.widget.TextView;

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
        TextView tv = holder.getTvNumber();
        tv.setText(String.valueOf(position + 1)); // 题号
        tv.setSelected(getItem(position).getBoolean(TTopic.finish)); // 完成(选中)情况
    }

}

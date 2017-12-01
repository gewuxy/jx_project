package jx.doctor.adapter.meeting;

import android.widget.TextView;

import lib.ys.adapter.AdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.TopicCaseVH;
import jx.doctor.model.meet.topic.Topic;
import jx.doctor.model.meet.topic.Topic.TTopic;

/**
 * 考试/问卷 题目情况Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicCaseAdapter extends AdapterEx<Topic, TopicCaseVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_topic_case_item;
    }

    @Override
    protected void refreshView(int position, TopicCaseVH holder) {
        TextView tv = holder.getTvNumber();
        tv.setText(String.valueOf(position + 1)); // 题号
        tv.setSelected(getItem(position).getBoolean(TTopic.finish)); // 完成(选中)情况
    }

}

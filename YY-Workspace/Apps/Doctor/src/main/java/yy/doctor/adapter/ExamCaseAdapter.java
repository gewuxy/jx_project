package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ExamCaseVH;
import yy.doctor.model.exam.Topic;
import yy.doctor.model.exam.Topic.TExamTopic;

/**
 * 考试情况Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamCaseAdapter extends AdapterEx<Topic, ExamCaseVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_case_item;
    }

    @Override
    protected void refreshView(int position, ExamCaseVH holder) {
        holder.getText().setText(String.valueOf(position + 1));
        holder.getText().setSelected(getItem(position).getBoolean(TExamTopic.finish));
    }

}

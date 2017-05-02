package yy.doctor.adapter;

import lib.ys.adapter.GroupAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ExamTopicVH;
import yy.doctor.model.exam.ExamTopic;
import yy.doctor.model.exam.GroupExamTopic;

import static yy.doctor.model.exam.ExamTopic.TExamTopic.answer;

/**
 * 考试题目Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicAdapter extends GroupAdapterEx<GroupExamTopic, ExamTopicVH> {

    @Override
    public int getGroupConvertViewResId() {
        return R.layout.layout_exam_topic_header;
    }

    @Override
    public void refreshGroupView(int groupPosition, boolean isExpanded, ExamTopicVH holder) {
        GroupExamTopic question = getGroup(groupPosition);
        holder.getTvQuestion().setText(question.getQuestion());
    }

    @Override
    public int getChildConvertViewResId() {
        return R.layout.layout_exam_topic_item;
    }

    @Override
    public void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, ExamTopicVH holder) {
        ExamTopic answers = getChild(groupPosition, childPosition);
        holder.getTvAnswer().setText(answers.getString(answer));
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getChildCount();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public ExamTopic getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getChild(childPosition);
    }
}

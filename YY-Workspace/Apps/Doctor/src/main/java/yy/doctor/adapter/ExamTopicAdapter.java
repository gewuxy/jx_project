package yy.doctor.adapter;

import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ExamTopicVH;

/**
 * 考试题目Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicAdapter extends AdapterEx<String, ExamTopicVH> {

    private List<Integer> mAnswers;//作答记录

    @Override
    protected void initView(int position, ExamTopicVH hol) {
        mAnswers = new ArrayList<>();
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_item;
    }

    @Override
    protected void refreshView(final int position, ExamTopicVH holder) {
        holder.getTvAnswer().setText(getItem(position));

        holder.getCbAnswer().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //记录答案
                if (isChecked) {
                    mAnswers.add(position);
                } else {
                    mAnswers.remove(Integer.valueOf(position));
                }
            }
        });
    }

    public List<Integer> getAnswers() {
        return mAnswers;
    }
}

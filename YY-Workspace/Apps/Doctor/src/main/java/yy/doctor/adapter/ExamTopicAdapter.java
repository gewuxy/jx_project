package yy.doctor.adapter;

import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import lib.ys.adapter.AdapterEx;
import lib.ys.view.NestCheckBox;
import yy.doctor.R;
import yy.doctor.adapter.VH.ExamTopicVH;

/**
 * 考试题目Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicAdapter extends AdapterEx<String, ExamTopicVH> {

    private List<Integer> mAnswer;//作答记录

    @Override
    protected void initView(int position, ExamTopicVH hol) {
        mAnswer = new ArrayList<>();
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_exam_topic_item;
    }

    @Override
    protected void refreshView(final int position, ExamTopicVH holder) {
        holder.getTvAnswer().setText(getItem(position));
        final NestCheckBox cbAnswer = holder.getCbAnswer();
        cbAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //记录答案
                if (isChecked)
                    mAnswer.add(position);
                else
                    mAnswer.remove(Integer.valueOf(position));
            }
        });
    }

    public List<Integer> getAnswer() {
        return mAnswer;
    }
}

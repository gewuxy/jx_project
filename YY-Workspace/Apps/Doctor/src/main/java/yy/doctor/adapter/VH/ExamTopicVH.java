package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicVH extends ViewHolderEx {
    public ExamTopicVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvAnswer() { return getView(R.id.exam_topic_tv_answer);}

    public TextView getTvQuestion() { return getView(R.id.exam_topic_tv_question);}
}

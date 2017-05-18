package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.view.NestCheckBox;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicVH extends ViewHolderEx {
    public TopicVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvAnswer() {
        return getView(R.id.exam_topic_tv_answer);
    }

    public NestCheckBox getCbAnswer() {
        return getView(R.id.exam_topic_cb_answer);
    }

}

package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicCaseVH extends ViewHolderEx {
    public TopicCaseVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getText() {
        return getView(R.id.exam_topic_case_tv_item);
    }
}

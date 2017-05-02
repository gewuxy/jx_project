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

public class ExamCaseVH extends ViewHolderEx {
    public ExamCaseVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getText() { return getView(R.id.exam_topic_case_tv_item);}
}

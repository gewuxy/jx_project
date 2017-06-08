package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
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

    public ImageView getIvAnswer() {
        return getView(R.id.exam_topic_iv_answer);
    }

    public LinearLayout getLayoutAnswer() {
        return getView(R.id.topic_item_layout);
    }

}

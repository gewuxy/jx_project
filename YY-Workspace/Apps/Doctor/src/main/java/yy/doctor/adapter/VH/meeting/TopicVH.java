package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @auther : GuoXuan
 * @since : 2017/11/16
 */

public class TopicVH extends ViewHolderEx {

    public TopicVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvTitle() {
        return findView(R.id.topic_tv_title);
    }

    public EditText getEtFill() {
        return findView(R.id.topic_et_fill);
    }

    public TextView getButton() {
        return findView(R.id.topic_tv_btn);
    }

    public ImageView getIvChoose() {
        return findView(R.id.topic_iv_choose);
    }

    public View getLayout() {
        return findView(R.id.topic_layout_choose);
    }

    public TextView getTvChoose() {
        return findView(R.id.topic_tv_choose);
    }

}

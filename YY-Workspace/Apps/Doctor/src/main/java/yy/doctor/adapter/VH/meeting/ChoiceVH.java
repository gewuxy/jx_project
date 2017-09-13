package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public class ChoiceVH extends ViewHolderEx {

    public ChoiceVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvAnswer() {
        return getView(R.id.answer_tv);
    }

    public ImageView getIvAnswer() {
        return getView(R.id.answer_iv);
    }

    public EditText getEtAnswer() {
        return getView(R.id.answer_et);
    }

}

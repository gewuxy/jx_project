package lib.yy.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.yy.R;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class FormVH extends ViewHolderEx {

    public FormVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvName() {
        return getView(R.id.form_tv_name);
    }

    public TextView getTvText() {
        return getView(R.id.form_tv_text);
    }

    public ImageView getIv() {
        return getView(R.id.form_iv);
    }

    public View getDivider() {
        return getView(R.id.form_divider);
    }

    public EditText getEt() {
        return getView(R.id.form_et);
    }

}

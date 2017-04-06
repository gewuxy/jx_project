package lib.yy.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import lib.yy.R;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class FormItemVH extends ViewHolderEx {

    public FormItemVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTv() {
        return getView(R.id.form_item_tv_name);
    }

    public ImageView getIv() {
        return getView(R.id.form_item_iv);
    }

    public View getDivider() {
        return getView(R.id.form_item_divider);
    }
}

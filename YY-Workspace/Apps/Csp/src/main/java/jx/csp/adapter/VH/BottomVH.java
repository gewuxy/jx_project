package jx.csp.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;

/**
 * @auther : GuoXuan
 * @since : 2017/9/1
 */
public class BottomVH extends ViewHolderEx {

    public BottomVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTv() {
        return getView(R.id.dialog_bottom_tv);
    }
}
package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonVH extends ViewHolderEx {

    public ThomsonVH(@NonNull View convertView) {
        super(convertView);
    }

    public LinearLayout getThomsonItemLayout() {
        return getView(R.id.thomson_item_layout);
    }

    public TextView getTv() {
        return getView(R.id.thomson_item_tv);
    }

    public TextView getTvSize() {
        return getView(R.id.thomson_item_tv_size);
    }

}

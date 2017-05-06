package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class OrderVH extends ViewHolderEx {

    public OrderVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvTime() {
        return getView(R.id.order_item_tv_time);
    }

}

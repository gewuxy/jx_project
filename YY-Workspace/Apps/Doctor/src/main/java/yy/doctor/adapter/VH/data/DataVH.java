package yy.doctor.adapter.VH.data;

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
public class DataVH extends ViewHolderEx {

    public DataVH(@NonNull View convertView) {
        super(convertView);
    }

    public LinearLayout getDataItemLayout() {
        return getView(R.id.data_item_layout);
    }

    public TextView getTvName() {
        return getView(R.id.data_item_tv_name);
    }

    public TextView getTvDetail() {
        return getView(R.id.data_item_tv_detail);
    }

}

package jx.doctor.adapter.VH.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.RecyclerViewHolderEx;
import jx.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/3
 */
public class FileVH extends RecyclerViewHolderEx {

    public FileVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTv() {
        return getView(R.id.unit_num_data_item_tv);
    }

}

package jx.doctor.adapter.VH.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import jx.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class UnitNumVH extends ViewHolderEx {

    public UnitNumVH(@NonNull View convertView) {
        super(convertView);
    }

    public LinearLayout getUnitNumItemLayout() {
        return getView(R.id.unit_num_item_layout);
    }

    public TextView getTvGroup() {
        return getView(R.id.unit_num_group_tv);
    }

    public NetworkImageView getIvChild() {
        return getView(R.id.unit_num_item_iv);
    }

    public TextView getTvChild() {
        return getView(R.id.unit_num_item_tv);
    }

}

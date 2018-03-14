package jx.csp.adapter.VH.contribution;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class HotUnitNumVH extends ViewHolderEx {

    public HotUnitNumVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getIv() {
        return getView(R.id.contribute_history_hot_unit_num_hot_unit_num_iv);
    }

    public View getItemLayout() {
        return getView(R.id.contribute_history_hot_unit_num_hot_unit_num_layout);
    }

    public TextView getTv() {
        return getView(R.id.contribute_history_hot_unit_num_hot_unit_num_tv_name);
    }
}

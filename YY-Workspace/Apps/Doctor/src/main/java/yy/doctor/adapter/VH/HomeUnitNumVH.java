package yy.doctor.adapter.VH;

import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/17
 */

public class HomeUnitNumVH extends RecyclerViewHolderEx {

    public HomeUnitNumVH(View itemView) {
        super(itemView);
    }

    public NetworkImageView getIv() {
        return getView(R.id.home_unit_num_item_iv);
    }

    public TextView getTvName() {
        return getView(R.id.home_unit_num_item_tv_name);
    }

    public TextView getTvAttention() {
        return getView(R.id.home_unit_num_item_tv_attention);
    }

}

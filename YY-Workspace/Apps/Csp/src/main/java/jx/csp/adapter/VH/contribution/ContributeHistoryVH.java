package jx.csp.adapter.VH.contribution;

import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeHistoryVH extends RecyclerViewHolderEx {

    public ContributeHistoryVH(View itemView) {
        super(itemView);
    }

    public View getItemLayout() {
        return getView(R.id.contribute_history_hot_unit_num_history_layout);
    }

    public NetworkImageView getIv() {
        return getView(R.id.contribute_history_hot_unit_num_history_iv);
    }

    public TextView getTvName() {
        return getView(R.id.contribute_history_hot_unit_num_history_tv_name);
    }

    public TextView getTvNum() {
        return getView(R.id.contribute_history_hot_unit_num_history_tv_num);
    }
}

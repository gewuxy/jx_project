package jx.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.doctor.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @author CaiXiang
 * @since 2018/3/1
 */

public class OverviewVH extends RecyclerViewHolderEx {

    public OverviewVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getIv() {
        return getView(R.id.overview_item_iv);
    }

    public TextView getTv() {
        return getView(R.id.overview_item_tv);
    }
}

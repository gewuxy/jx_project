package jx.csp.adapter.VH.contribution;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class SelectPlatformVH extends ViewHolderEx {

    public SelectPlatformVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getIv() {
        return getView(R.id.select_platform_item_iv);
    }

    public TextView getTvName() {
        return getView(R.id.select_platform_item_tv_name);
    }

    public TextView getTvContribution() {
        return getView(R.id.select_platform_item_tv_contribution);
    }
}

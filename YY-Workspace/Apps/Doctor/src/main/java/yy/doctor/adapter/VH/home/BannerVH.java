package yy.doctor.adapter.VH.home;

import android.support.annotation.NonNull;
import android.view.View;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerVH extends ViewHolderEx {

    public BannerVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getIv() {
        return getView(R.id.banner_view_iv);
    }

    public View getLayoutRoot() {
        return getView(R.id.banner_view_layout_root);
    }

}

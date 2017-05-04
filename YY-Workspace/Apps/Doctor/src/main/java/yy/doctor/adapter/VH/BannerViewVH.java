package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;

import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerViewVH extends ViewHolderEx {


    public BannerViewVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getIv(){
        return getView(R.id.banner_view_iv);
    }

}

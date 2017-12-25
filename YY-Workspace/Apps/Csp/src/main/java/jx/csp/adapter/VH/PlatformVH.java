package jx.csp.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @auther HuoXuYu
 * @since 2017/9/28
 */

public class PlatformVH extends ViewHolderEx {

    public PlatformVH(@NonNull View convertView) {
        super(convertView);
    }

    public View getItemLayout() {
        return getView(R.id.platform_item_layout);
    }

    public ImageView getIvSelect() {
        return getView(R.id.platform_item_iv_select);
    }

    public NetworkImageView getIv() {
        return getView(R.id.platform_item_iv);
    }

    public TextView getTvName() {
        return getView(R.id.platform_item_tv_name);
    }

    public TextView getTvText() {
        return getView(R.id.platform_item_tv_text);
    }
}

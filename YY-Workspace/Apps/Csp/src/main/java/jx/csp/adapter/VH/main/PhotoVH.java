package jx.csp.adapter.VH.main;

import android.view.View;
import android.widget.ImageView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @auther : GuoXuan
 * @since : 2018/2/1
 */
public class PhotoVH extends RecyclerViewHolderEx {

    public PhotoVH(View itemView) {
        super(itemView);
    }

    public NetworkImageView getIvPic() {
        return findView(R.id.photo_iv_pic);
    }

    public ImageView getIvSelect() {
        return findView(R.id.photo_iv_select);
    }

    public View getIvCover() {
        return findView(R.id.photo_iv_cover);
    }

}

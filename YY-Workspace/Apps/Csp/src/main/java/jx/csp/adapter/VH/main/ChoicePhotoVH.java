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
public class ChoicePhotoVH extends RecyclerViewHolderEx {

    public ChoicePhotoVH(View itemView) {
        super(itemView);
    }

    public View getDividerBottom() {
        return findView(R.id.choice_photo_divider_bottom);
    }

    public View getLayoutAdd() {
        return findView(R.id.choice_photo_layout_add_camera);
    }

    public NetworkImageView getIvPhoto() {
        return findView(R.id.choice_photo_iv_photo);
    }

    public ImageView getIvDelete() {
        return findView(R.id.choice_photo_iv_delete);
    }

}

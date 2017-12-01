package jx.doctor.adapter.VH.meeting;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import jx.doctor.R;

/**
 * @auther : GuoXuan
 * @since : 2017/9/27
 */

public class CourseVH extends RecyclerViewHolderEx {

    public CourseVH(View itemView) {
        super(itemView);
    }

    public NetworkImageView getIvPPT() {
        return getView(R.id.breviary_iv_ppt);
    }

    public View getLayoutMedia() {
        return getView(R.id.breviary_layout_media);
    }

    public ImageView getIvMedia() {
        return getView(R.id.breviary_iv_media);
    }

    public TextView getTvMedia() {
        return getView(R.id.breviary_tv_media);
    }

}

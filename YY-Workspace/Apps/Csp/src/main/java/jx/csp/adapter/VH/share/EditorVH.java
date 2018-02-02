package jx.csp.adapter.VH.share;

import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class EditorVH extends RecyclerViewHolderEx {

    public EditorVH(View itemView) {
        super(itemView);
    }

    public View getItemLayout() {
        return getView(R.id.editor_theme_layout);
    }

    public NetworkImageView getItemIv() {
        return getView(R.id.editor_theme_iv_item);
    }

    public TextView getItemText() {
        return getView(R.id.editor_preview_tv_item);
    }

}

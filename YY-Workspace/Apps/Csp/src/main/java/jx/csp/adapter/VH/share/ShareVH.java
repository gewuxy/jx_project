package jx.csp.adapter.VH.share;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class ShareVH extends RecyclerViewHolderEx{

    public ShareVH(View itemView) {
        super(itemView);
    }

    public View getItemLayout() {
        return getView(R.id.dialog_layout_share);
    }

    public ImageView getIvIcon() {
        return getView(R.id.dialog_share_iv_wechat);
    }

    public TextView getTvName() {
        return getView(R.id.dialog_share_tv_wechat);
    }
}

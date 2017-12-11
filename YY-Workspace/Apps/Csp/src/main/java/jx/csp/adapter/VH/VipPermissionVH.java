package jx.csp.adapter.VH;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;

/**
 * @auther Huoxuyu
 * @since 2017/12/8
 */

public class VipPermissionVH extends RecyclerViewHolderEx{

    public VipPermissionVH(View itemView) {
        super(itemView);
    }

    public View getItemLayout(){
        return getView(R.layout.layout_vip_permission);
    }

    public ImageView getIvImage(){
        return getView(R.id.iv_vip_image);
    }

    public TextView getTvText(){
        return getView(R.id.tv_vip_text);
    }
}

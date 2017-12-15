package jx.csp.adapter;

import jx.csp.R;
import jx.csp.adapter.VH.VipPermissionVH;
import jx.csp.constant.VipPermission;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * @auther Huoxuyu
 * @since 2017/12/8
 */

public class VipPermissionAdapter extends RecyclerAdapterEx<VipPermission, VipPermissionVH> {

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_vip_permission;
    }

    @Override
    protected void refreshView(int position, VipPermissionVH holder) {
        VipPermission item = getItem(position);
        holder.getIvImage().setImageResource(item.image());
        holder.getTvText().setText(item.text());
        holder.getTvText().setTextColor(item.color());
    }

}

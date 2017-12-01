package jx.doctor.adapter.user;

import lib.ys.adapter.AdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.user.PcdVH;
import jx.doctor.model.Pcd;
import jx.doctor.model.Pcd.TPcd;

/**
 * @auther Huoxuyu
 * @since 2017/7/4
 */

public class PcdAdapter extends AdapterEx<Pcd, PcdVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_pcd_item;
    }

    @Override
    protected void refreshView(int position, PcdVH holder) {
        Pcd item = getItem(position);

        holder.getTv().setText(item.getString(TPcd.name));
        if (item.getInt(TPcd.level) == Pcd.KLevelEnd) {
            goneView(holder.getIvArrow());
        } else {
            showView(holder.getIvArrow());
        }

        setOnViewClickListener(position, holder.getLayout());
    }
}

package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DataVH;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;

/**
 * @auther WangLan
 * @since 2017/7/31
 */

public class CollectionDrugAdapter  extends AdapterEx<ThomsonDetail, DataVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_data_item;
    }

    @Override
    protected void refreshView(int position, DataVH holder) {
        if (position != 0) {
            goneView(holder.getDivider());
        }
        ThomsonDetail item = getItem(position);
        holder.getTvName().setText(getItem(position).getString(TThomsonDetail.title));
        String size = item.getLong(TThomsonDetail.fileSize) + "K";
        holder.getTvDetail().setText(size);

    }


}

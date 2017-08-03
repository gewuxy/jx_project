package yy.doctor.adapter.data;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DataVH;
import yy.doctor.model.data.DrugCategoryData;
import yy.doctor.model.data.DrugCategoryData.TCategoryData;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DrugCategoryListAdapter extends AdapterEx<DrugCategoryData, DataVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_data_item;
    }

    @Override
    protected void refreshView(int position, DataVH holder) {
        if (position != 0) {
            goneView(holder.getDivider());
        }
        goneView(holder.getTvDetail());

        DrugCategoryData item = getItem(position);
        item.getEv(TCategoryData.isFile);
        holder.getTvName().setText(getItem(position).getString(TCategoryData.isFile));
        String size = item.getLong(TCategoryData.isFile) + "K";
        holder.getTvDetail().setText(size);
    }
}

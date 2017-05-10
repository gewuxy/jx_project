package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ProvinceVH;
import yy.doctor.model.Province;

import static yy.doctor.model.Province.TProvince.name;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ProvinceAdapter extends AdapterEx<Province, ProvinceVH> {

    private int mSelectedPos = 0;

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_province_item;
    }

    @Override
    protected void refreshView(int position, ProvinceVH holder) {
        holder.getTvProvince().setText(getItem(position).getString(name));

        if (mSelectedPos == position) {
            holder.getTvProvince().setSelected(true);
            showView(holder.getIndicator());
        } else {
            holder.getTvProvince().setSelected(false);
            goneView(holder.getIndicator());
        }
    }

    public void setSelectedPosition(int p) {
        mSelectedPos = p;
        notifyDataSetChanged();
    }
}

package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.SectionVH;

/**
 * @author CaiXiang
 * @since 2017/5/25
 */

public class TitleGradeAdapter extends AdapterEx<String, SectionVH> {

    private int mSelectedItem = 0;

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_province_item;
    }

    public void setSelectItem(int selectItem) {
        mSelectedItem = selectItem;
        notifyDataSetChanged();
    }

    @Override
    protected void refreshView(int position, SectionVH holder) {
        holder.getTvProvince().setText(getItem(position));
        if (mSelectedItem == position) {
            holder.getTvProvince().setSelected(true);
            showView(holder.getV());
        } else {
            holder.getTvProvince().setSelected(false);
            goneView(holder.getV());
        }
    }

}

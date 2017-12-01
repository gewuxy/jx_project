package jx.doctor.adapter.user;

import lib.ys.adapter.AdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.user.SectionVH;

/**
 * 职称的adapter（一级）
 *
 * @author CaiXiang
 * @since 2017/5/25
 */

public class TitleGradeAdapter extends AdapterEx<String, SectionVH> {

    private int mSelectedItem = 0;

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_title_category_item;
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

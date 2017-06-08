package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.view.ViewUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.SectionVH;
import yy.doctor.model.Section;
import yy.doctor.model.Section.TSection;

/**
 * @author CaiXiang
 * @since 2017/5/2
 */
public class SectionCategoryAdapter extends AdapterEx<Section, SectionVH> {

    private int mSelectedItem = 0;

    public void setSelectItem(int selectItem) {
        mSelectedItem = selectItem;
        notifyDataSetChanged();
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_province_item;
    }

    @Override
    protected void refreshView(int position, SectionVH holder) {
        holder.getTvProvince().setText(getItem(position).getString(TSection.category));
        if (mSelectedItem == position) {
            holder.getTvProvince().setSelected(true);
            ViewUtil.showView(holder.getV());
        } else {
            holder.getTvProvince().setSelected(false);
            ViewUtil.goneView(holder.getV());
        }
    }
}

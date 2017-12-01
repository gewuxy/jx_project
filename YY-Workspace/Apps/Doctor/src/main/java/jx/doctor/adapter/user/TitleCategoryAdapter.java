package jx.doctor.adapter.user;

import lib.ys.adapter.AdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.user.SectionDetailVH;

/**
 * 职称的adapter（二级）
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
public class TitleCategoryAdapter extends AdapterEx<String, SectionDetailVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_title_grade_item;
    }

    @Override
    protected void refreshView(int position, SectionDetailVH holder) {
        holder.getTvCity().setText(getItem(position));
    }
}

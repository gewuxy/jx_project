package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.SectionDetailVH;

/**
 * 科室的adapter（二级）
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
public class SectionNameAdapter extends AdapterEx<String, SectionDetailVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_title_grade_item;
    }

    @Override
    protected void refreshView(int position, SectionDetailVH holder) {
        holder.getTvCity().setText(getItem(position));
    }
}

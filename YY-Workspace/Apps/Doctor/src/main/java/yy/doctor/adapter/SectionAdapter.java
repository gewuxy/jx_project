package yy.doctor.adapter;

import lib.ys.adapter.recycler.RecyclerAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.SectionVH;

/**
 * 科室列表的Adapter
 * <p>
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class SectionAdapter extends RecyclerAdapterEx<String, SectionVH> {

    @Override
    protected void refreshView(int position, SectionVH holder) {
        holder.getTv().setText(getItem(position));
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_meeting_section_item;
    }
}

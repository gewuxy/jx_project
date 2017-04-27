package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ThomsonLibVH;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonLibAdapter extends AdapterEx<String, ThomsonLibVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_thomson_lib_item;
    }

    @Override
    protected void refreshView(int position, ThomsonLibVH holder) {

    }

}

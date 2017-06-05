package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ThomsonVH;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonAdapter extends AdapterEx<String, ThomsonVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_thomson_item;
    }

    @Override
    protected void refreshView(int position, ThomsonVH holder) {

    }

}

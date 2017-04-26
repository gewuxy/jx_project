package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.UnitNumVH;

/**
 * @auther yuansui
 * @since 2017/4/25
 */

public class UnitNumAdapter extends AdapterEx<String, UnitNumVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_unit_num_detail_item;
    }

    @Override
    protected void refreshView(int position, UnitNumVH holder) {

    }
}

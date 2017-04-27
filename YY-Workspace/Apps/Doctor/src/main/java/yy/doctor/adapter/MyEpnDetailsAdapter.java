package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.UpdateLogVH;

/**
 * @author CaiXiang
 * @since 2017/4/26
 */
public class MyEpnDetailsAdapter extends AdapterEx<String,UpdateLogVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_my_epn_details_item;
    }

    @Override
    protected void refreshView(int position, UpdateLogVH holder) {

    }

}

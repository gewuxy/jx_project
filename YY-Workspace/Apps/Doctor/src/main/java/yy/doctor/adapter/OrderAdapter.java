package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.OrderVH;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class OrderAdapter extends AdapterEx<String,OrderVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_order_item;
    }

    @Override
    protected void refreshView(int position, OrderVH holder) {

    }

}

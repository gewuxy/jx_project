package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.UpdateLogVH;

/**
 * @author CaiXiang
 * @since 2017/4/26
 */
public class UpdateLogAdapter extends AdapterEx<String,UpdateLogVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_update_log_item;
    }

    @Override
    protected void refreshView(int position, UpdateLogVH holder) {

    }

}

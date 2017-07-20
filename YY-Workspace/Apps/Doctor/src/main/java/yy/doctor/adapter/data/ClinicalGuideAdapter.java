package yy.doctor.adapter.data;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DataVH;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class ClinicalGuideAdapter extends AdapterEx<String, DataVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_data_item;
    }

    @Override
    protected void refreshView(int position, DataVH holder) {

        goneView(holder.getTvDetail());
    }

}

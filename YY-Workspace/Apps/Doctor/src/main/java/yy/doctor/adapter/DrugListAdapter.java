package yy.doctor.adapter;

import android.view.View;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.DataVH;
import yy.doctor.ui.activity.data.DrugDetailActivity;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DrugListAdapter extends AdapterEx<String, DataVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_data_item;
    }

    @Override
    protected void refreshView(int position, DataVH holder) {

        goneView(holder.getTvDetail());

        setOnViewClickListener(position, holder.getDataItemLayout());
    }

    @Override
    protected void onViewClick(int position, View v) {

        startActivity(DrugDetailActivity.class);
    }
}

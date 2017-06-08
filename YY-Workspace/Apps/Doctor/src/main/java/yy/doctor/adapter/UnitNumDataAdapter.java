package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.UnitNumDataVH;
import yy.doctor.model.unitnum.UnitNumDetailData;
import yy.doctor.model.unitnum.UnitNumDetailData.TUnitNumDetailData;

/**
 * @author CaiXiang
 * @since 2017/5/3
 */
public class UnitNumDataAdapter extends AdapterEx<UnitNumDetailData, UnitNumDataVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_unit_num_data_item;
    }

    @Override
    protected void refreshView(int position, UnitNumDataVH holder) {


        holder.getTv().setText(getItem(position).getString(TUnitNumDetailData.materialName));

    }

}

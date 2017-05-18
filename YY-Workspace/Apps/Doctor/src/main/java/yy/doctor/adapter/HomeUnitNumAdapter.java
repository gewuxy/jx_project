package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.recycler.RecyclerAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.HomeUnitNumVH;
import yy.doctor.model.home.RecUnitNum;
import yy.doctor.model.home.RecUnitNum.TRecUnitNum;

/**
 * @author CaiXiang
 * @since 2017/5/17
 */

public class HomeUnitNumAdapter extends RecyclerAdapterEx<RecUnitNum, HomeUnitNumVH> {
    @Override
    protected void refreshView(int position, HomeUnitNumVH holder) {

        List<RecUnitNum> list = getData();
        holder.getTvName().setText(list.get(position).getString(TRecUnitNum.nickname));
        holder.getIv().placeHolder(R.mipmap.ic_default_home_unit_num).load();
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_home_unit_num_item;
    }
}

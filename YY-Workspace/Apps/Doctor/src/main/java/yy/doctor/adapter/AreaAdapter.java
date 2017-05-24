package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.view.ViewUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.ProvinceCityAreaVH;
import yy.doctor.model.Area;
import yy.doctor.model.Area.TArea;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class AreaAdapter extends AdapterEx<Area, ProvinceCityAreaVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_province_city_area_item;
    }

    @Override
    protected void refreshView(int position, ProvinceCityAreaVH holder) {

        ViewUtil.goneView(holder.getIvArrow());
        holder.getTv().setText(getItem(position).getString(TArea.name));
        setOnViewClickListener(position, holder.getLayout());
    }

}

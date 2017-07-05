package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.view.ViewUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.ProvinceCityAreaVH;
import yy.doctor.model.Provinces;
import yy.doctor.model.Provinces.TProvinces;

/**
 * @auther Huoxuyu
 * @since 2017/7/4
 */

public class ProvincesAdapter extends AdapterEx<Provinces, ProvinceCityAreaVH>{
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_province_city_area_item;
    }

    @Override
    protected void refreshView(int position, ProvinceCityAreaVH holder) {

        Provinces provinces = getItem(position);
        ViewUtil.goneView(holder.getIvArrow());
        holder.getTv().setText(provinces.getString(TProvinces.name));
        if (provinces.getInt(TProvinces.level) == 3) {
            ViewUtil.goneView(holder.getIvArrow());
        }else {
            ViewUtil.showView(holder.getIvArrow());
        }
        setOnViewClickListener(position, holder.getLayout());
    }
}

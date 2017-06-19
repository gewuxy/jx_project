package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.view.ViewUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.ProvinceCityAreaVH;
import yy.doctor.model.City;
import yy.doctor.model.City.TCity;

/**
 * 城市的adapter
 *
 * @author CaiXiang
 * @since 2017/4/28
 */
public class CityAdapter extends AdapterEx<City, ProvinceCityAreaVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_province_city_area_item;
    }

    @Override
    protected void refreshView(int position, ProvinceCityAreaVH holder) {

        City city = getItem(position);
        holder.getTv().setText(city.getString(TCity.name));
        if (city.getInt(TCity.level) == 3) {
            ViewUtil.goneView(holder.getIvArrow());
        } else {
            ViewUtil.showView(holder.getIvArrow());
        }
        setOnViewClickListener(position, holder.getLayout());
    }

}

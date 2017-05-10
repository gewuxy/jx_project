package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.CityVH;
import yy.doctor.model.City;
import yy.doctor.model.City.TCity;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class CityAdapter extends AdapterEx<City, CityVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_city_item;
    }

    @Override
    protected void refreshView(int position, CityVH holder) {
        holder.getTvCity().setText(getItem(position).getString(TCity.name));
    }
}

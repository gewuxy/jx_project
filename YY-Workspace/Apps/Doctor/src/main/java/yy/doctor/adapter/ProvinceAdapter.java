package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ProvinceCityAreaVH;
import yy.doctor.model.Province;
import yy.doctor.model.Province.TProvince;

/**
 * 省份的adapter
 *
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ProvinceAdapter extends AdapterEx<Province, ProvinceCityAreaVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_province_city_area_item;
    }

    @Override
    protected void refreshView(int position, ProvinceCityAreaVH holder) {

        Province province = getItem(position);
        holder.getTv().setText(province.getString(TProvince.name));
        setOnViewClickListener(position, holder.getLayout());
    }

}

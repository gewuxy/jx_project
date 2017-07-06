package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.view.ViewUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.ProvinceCityAreaVH;
import yy.doctor.model.Pca;
import yy.doctor.model.Pca.TPca;

/**
 * @auther Huoxuyu
 * @since 2017/7/4
 */

public class PcaAdapter extends AdapterEx<Pca, ProvinceCityAreaVH>{
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_province_city_area_item;
    }

    @Override
    protected void refreshView(int position, ProvinceCityAreaVH holder) {

        Pca item = getItem(position);
        ViewUtil.goneView(holder.getIvArrow());
        holder.getTv().setText(item.getString(TPca.name));
        if (item.getInt(TPca.level) == 3) {
            ViewUtil.goneView(holder.getIvArrow());
        }else {
            ViewUtil.showView(holder.getIvArrow());
        }
        setOnViewClickListener(position, holder.getLayout());
    }
}

package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ProvinceCityAreaVH extends ViewHolderEx {


    public ProvinceCityAreaVH(@NonNull View convertView) {
        super(convertView);
    }

    public RelativeLayout getLayout() {
        return getView(R.id.layout_province_city_area_item_layout);
    }

    public TextView getTv() {
        return getView(R.id.layout_province_city_area_item_tv);
    }

    public ImageView getIvArrow() {
        return getView(R.id.layout_province_city_area_item_iv);
    }

}

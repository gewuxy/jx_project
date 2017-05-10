package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ProvinceVH extends ViewHolderEx{

    public ProvinceVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvProvince() {
        return getView(R.id.province_tv);
    }

    public View getIndicator() {
        return getView(R.id.province_layout_indicator);
    }

}

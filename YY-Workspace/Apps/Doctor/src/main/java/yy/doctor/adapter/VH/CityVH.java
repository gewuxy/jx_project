package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class CityVH extends ViewHolderEx{

    public CityVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvCity() {
        return getView(R.id.city_tv);
    }

}

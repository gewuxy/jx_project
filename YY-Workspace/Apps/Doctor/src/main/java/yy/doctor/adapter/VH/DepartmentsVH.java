package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/2
 */
public class DepartmentsVH extends ViewHolderEx {

    public DepartmentsVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvProvince() {
        return getView(R.id.province_tv);
    }

    public View getV() {
        return getView(R.id.province_layout_indicator);
    }

}

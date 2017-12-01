package jx.doctor.adapter.VH.user;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import jx.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/19
 */

public class HospitalBaiDuVH extends ViewHolderEx {

    public HospitalBaiDuVH(@NonNull View convertView) {
        super(convertView);
    }

    public LinearLayout getItemLayout() {
        return getView(R.id.hospital_baidu_item_layout);
    }

    public TextView getTvHospitalName() {
        return getView(R.id.hospital_name);
    }

    public TextView getTvHospitalDistance() {
        return getView(R.id.hospital_distance);
    }

    public TextView getTvHospitalAddress() {
        return getView(R.id.hospital_address);
    }

    public TextView getTvHospitalMargin() {
        return getView(R.id.hospital_tv_margin);
    }

}

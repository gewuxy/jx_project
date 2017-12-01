package jx.doctor.adapter.user;

import lib.ys.adapter.MultiAdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.user.HospitalBaiDuVH;
import jx.doctor.model.hospital.Hospital;
import jx.doctor.model.hospital.Hospital.THospital;
import jx.doctor.model.hospital.HospitalTitle;
import jx.doctor.model.hospital.HospitalTitle.TText;
import jx.doctor.model.hospital.IHospital;
import jx.doctor.model.hospital.IHospital.HospitalType;

/**
 * @auther WangLan
 * @since 2017/7/19
 */

public class HospitalAdapter extends MultiAdapterEx<IHospital, HospitalBaiDuVH> {

    @Override
    protected void refreshView(int position, HospitalBaiDuVH holder, int itemType) {

        switch (itemType) {
            case HospitalType.hospital_title: {
                HospitalTitle item = (HospitalTitle) getItem(position);
                holder.getTvHospitalMargin().setText(item.getString(TText.name));
            }
            break;
            case HospitalType.hospital_data: {
                Hospital hospital = (Hospital) getItem(position);
                holder.getTvHospitalName().setText(hospital.getString(THospital.name));
                holder.getTvHospitalAddress().setText(hospital.getString(THospital.address));
                holder.getTvHospitalDistance().setText(hospital.getString(THospital.distance)+"m");
                setOnViewClickListener(position, holder.getItemLayout());
            }
            break;
        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        int id = 0;
        switch (itemType) {
            case HospitalType.hospital_title: {
                id = R.layout.activity_hospity_recommend;
            }
            break;
            case HospitalType.hospital_data: {
                id = R.layout.layout_hospital_item;
            }
            break;
        }
        return id;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return HospitalType.class.getDeclaredFields().length;
    }
}

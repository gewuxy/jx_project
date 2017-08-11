package yy.doctor.adapter;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.HospitalBaiDuVH;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.model.hospital.Hospital.THospital;
import yy.doctor.model.hospital.HospitalTitle;
import yy.doctor.model.hospital.HospitalTitle.TText;
import yy.doctor.model.hospital.IHospital;
import yy.doctor.model.hospital.IHospital.HospitalType;

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
                holder.getTvHospitalDistance().setText(hospital.getString(THospital.distance));
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

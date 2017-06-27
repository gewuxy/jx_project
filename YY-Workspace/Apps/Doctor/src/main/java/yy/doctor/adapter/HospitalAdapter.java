package yy.doctor.adapter;

import lib.ys.adapter.GroupAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.HospitalVH;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital;

import static yy.doctor.model.hospital.Hospital.THospital.name;

/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class HospitalAdapter extends GroupAdapterEx<GroupHospital, Hospital, HospitalVH> {

    @Override
    public int getGroupConvertViewResId() {
        return R.layout.layout_hospital_group;
    }

    @Override
    public void refreshGroupView(int groupPosition, boolean isExpanded, HospitalVH holder) {
        GroupHospital item = getGroup(groupPosition);
        holder.getTvGroup().setText(item.getTag());
    }

    @Override
    public int getChildConvertViewResId() {
        return R.layout.layout_hospital_child;
    }

    @Override
    public void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, HospitalVH holder) {
        Hospital item = getChild(groupPosition, childPosition);
        holder.getTvChild().setText(item.getString(name));
    }
}

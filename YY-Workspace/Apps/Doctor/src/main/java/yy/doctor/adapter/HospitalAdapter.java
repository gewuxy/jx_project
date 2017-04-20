package yy.doctor.adapter;

import lib.ys.adapter.GroupAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.HospitalVH;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital;

/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalAdapter extends GroupAdapterEx<GroupHospital, HospitalVH> {

    @Override
    public int getGroupConvertViewResId() {
        return R.layout.layout_hospital_group;
    }

    @Override
    public void refreshGroupView(int groupPosition, boolean isExpanded, HospitalVH holder) {
        GroupHospital item = getGroup(groupPosition);
    }

    @Override
    public int getChildConvertViewResId() {
        return R.layout.layout_hospital_child;
    }

    @Override
    public void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, HospitalVH holder) {
        Hospital item = getChild(groupPosition, childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getChildCount();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Hospital getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getChild(childPosition);
    }
}

package jx.doctor.adapter.data;

import android.view.View;

import lib.ys.adapter.GroupAdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.data.DataUnitDetailVH;
import jx.doctor.model.data.DataUnitDetail;
import jx.doctor.model.data.DataUnitDetail.TDataUnitDetail;
import jx.doctor.model.data.GroupDataUnitDetail;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DataUnitDetailAdapter extends GroupAdapterEx<GroupDataUnitDetail, DataUnitDetail, DataUnitDetailVH> {

    @Override
    public int getGroupConvertViewResId() {
        return R.layout.layout_data_unit_detail_group;
    }

    @Override
    public void refreshGroupView(int groupPosition, boolean isExpanded, DataUnitDetailVH holder) {

        holder.getIv().setSelected(isExpanded);
        GroupDataUnitDetail group = getGroup(groupPosition);
        holder.getTvName().setText(group.getTag());
    }

    @Override
    public void onGroupViewClick(int groupPosition, View v) {
    }

    @Override
    public int getChildConvertViewResId() {
        return R.layout.layout_drug_detail_item;
    }

    @Override
    public void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, DataUnitDetailVH holder) {
        DataUnitDetail item = getChild(groupPosition, childPosition);
        holder.getTvDetail().setText(item.getString(TDataUnitDetail.detailValue));
    }

}

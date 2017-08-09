package yy.doctor.adapter.data;

import android.view.View;

import lib.ys.adapter.GroupAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DataUnitDetailVH;
import yy.doctor.model.data.DataUnitDetail;
import yy.doctor.model.data.DataUnitDetail.TDataUnitDetail;
import yy.doctor.model.data.GroupDataUnitDetail;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DataUnitDetailAdapter extends GroupAdapterEx<GroupDataUnitDetail, DataUnitDetail, DataUnitDetailVH> {

    // FIXME: 布局文件id需要修改
    @Override
    public int getGroupConvertViewResId() {
        return R.layout.layout_data_unit_detail_group;
    }

    @Override
    public void refreshGroupView(int groupPosition, boolean isExpanded, DataUnitDetailVH holder) {
        if (isExpanded) {
            holder.getIv().setSelected(true);
        } else {
            holder.getIv().setSelected(false);
        }
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

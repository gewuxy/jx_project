package yy.doctor.adapter;

import lib.ys.adapter.GroupAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.UnitNumVH;
import yy.doctor.model.unitnum.GroupUnitNum;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.model.unitnum.UnitNum.TUnitNum;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class UnitNumAdapter extends GroupAdapterEx<GroupUnitNum, UnitNumVH> {

    @Override
    public int getGroupConvertViewResId() {
        return R.layout.layout_unit_num_group;
    }

    @Override
    public void refreshGroupView(int groupPosition, boolean isExpanded, UnitNumVH holder) {

        GroupUnitNum group = getGroup(groupPosition);

        holder.getTvGroup().setText(group.getLetter());

    }

    @Override
    public int getChildConvertViewResId() {
        return R.layout.layout_unit_num_item;
    }

    @Override
    public void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, UnitNumVH holder) {

        UnitNum item=getGroup(groupPosition).getChild(childPosition);

        holder.getIvChild().placeHolder(R.mipmap.ic_default_epc).load();
        holder.getTvChild().setText(item.getString(TUnitNum.nickname));

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
    public UnitNum getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getChild(childPosition);
    }

}

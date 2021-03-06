package jx.doctor.adapter.me;

import android.view.View;

import lib.ys.adapter.GroupAdapterEx;
import lib.ys.network.image.shape.CircleRenderer;
import jx.doctor.R;
import jx.doctor.adapter.VH.me.UnitNumVH;
import jx.doctor.model.unitnum.GroupUnitNum;
import jx.doctor.model.unitnum.UnitNum;
import jx.doctor.model.unitnum.UnitNum.TUnitNum;
import jx.doctor.ui.activity.me.unitnum.UnitNumDetailActivityRouter;

/**
 * 单位号的adapter
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class UnitNumAdapter extends GroupAdapterEx<GroupUnitNum, UnitNum, UnitNumVH> {

    @Override
    public int getGroupConvertViewResId() {
        return R.layout.layout_unit_num_group;
    }

    @Override
    public void refreshGroupView(int groupPosition, boolean isExpanded, UnitNumVH holder) {
        GroupUnitNum group = getGroup(groupPosition);

        holder.getTvGroup().setText(group.getTag());
    }

    @Override
    public int getChildConvertViewResId() {
        return R.layout.layout_unit_num_item;
    }

    @Override
    public void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, UnitNumVH holder) {
        UnitNum item = getChild(groupPosition, childPosition);

        holder.getIvChild().placeHolder(R.drawable.ic_default_unit_num)
                .renderer(new CircleRenderer())
                .url(item.getString(TUnitNum.headimg))
                .load();
        holder.getTvChild().setText(item.getString(TUnitNum.nickname));

        setOnChildViewClickListener(groupPosition, childPosition, holder.getUnitNumItemLayout());
    }

    @Override
    public void onChildViewClick(int groupPosition, int childPosition, View v) {

        UnitNum item = getChild(groupPosition, childPosition);
        UnitNumDetailActivityRouter.create(item.getInt(TUnitNum.id)).route(getContext());
    }
}

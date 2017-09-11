package yy.doctor.adapter.me;

import android.view.View;

import lib.ys.adapter.GroupAdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import yy.doctor.R;
import yy.doctor.adapter.VH.me.UnitNumVH;
import yy.doctor.model.unitnum.GroupUnitNum;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.model.unitnum.UnitNum.TUnitNum;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivity;

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
        UnitNumDetailActivity.nav(getContext(), item.getInt(TUnitNum.id));
    }
}

package jx.csp.adapter.contribution;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.SelectPlatformVH;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.shape.CircleRenderer;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class SelectPlatformAdapter extends AdapterEx<UnitNum, SelectPlatformVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_select_platform_item;
    }

    @Override
    protected void refreshView(int position, SelectPlatformVH holder) {

        UnitNum item = getItem(position);
        holder.getTvName().setText(item.getString(TUnitNum.name));
        holder.getIv()
                .placeHolder(R.drawable.ic_default_unit_num)
                .renderer(new CircleRenderer())
                .load();
        setOnViewClickListener(position, holder.getTvContribution());
    }
}

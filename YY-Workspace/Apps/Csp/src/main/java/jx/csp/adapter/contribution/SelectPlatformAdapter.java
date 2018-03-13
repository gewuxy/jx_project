package jx.csp.adapter.contribution;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.SelectPlatformVH;
import jx.csp.model.contribution.SelectPlatform;
import jx.csp.model.contribution.SelectPlatform.TSelectPlatform;
import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.shape.CircleRenderer;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class SelectPlatformAdapter extends AdapterEx<SelectPlatform, SelectPlatformVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_select_platform_item;
    }

    @Override
    protected void refreshView(int position, SelectPlatformVH holder) {

        SelectPlatform item = getItem(position);
        holder.getTvName().setText(item.getString(TSelectPlatform.platformName));
        holder.getIv()
                .placeHolder(R.drawable.ic_default_unit_num)
                .url(item.getString(TSelectPlatform.imgUrl))
                .renderer(new CircleRenderer())
                .load();
        setOnViewClickListener(position, holder.getTvContribution());
    }
}

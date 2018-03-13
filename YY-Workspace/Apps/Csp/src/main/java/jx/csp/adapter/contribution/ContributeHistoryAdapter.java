package jx.csp.adapter.contribution;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.ContributeHistoryVH;
import jx.csp.model.contribution.ContributeHistory;
import jx.csp.model.contribution.ContributeHistory.TContributeHistory;
import lib.ys.YSLog;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.network.image.shape.CircleRenderer;
import lib.ys.util.res.ResLoader;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeHistoryAdapter extends RecyclerAdapterEx<ContributeHistory, ContributeHistoryVH> {

    @Override
    protected void refreshView(int position, ContributeHistoryVH holder) {
        ContributeHistory item = getItem(position);

        holder.getIv()
                .placeHolder(R.drawable.ic_default_unit_num)
                .url(item.getString(TContributeHistory.headimg))
                .renderer(new CircleRenderer())
                .load();
        YSLog.d(TAG, "tttttt" + item.getString(TContributeHistory.acceptName));
        holder.getTvName().setText(item.getString(TContributeHistory.acceptName));
        holder.getTvNum().setText(String.format(ResLoader.getString(R.string.already_contribution), item.getInt(TContributeHistory.acceptCount)));
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_contribute_history_hot_unit_num_history_item;
    }
}

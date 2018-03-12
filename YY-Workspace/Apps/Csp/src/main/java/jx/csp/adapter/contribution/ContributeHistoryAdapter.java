package jx.csp.adapter.contribution;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.ContributeHistoryVH;
import jx.csp.model.contribution.ContributeHistory;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeHistoryAdapter extends RecyclerAdapterEx<ContributeHistory, ContributeHistoryVH> {

    @Override
    protected void refreshView(int position, ContributeHistoryVH holder) {

        holder.getIv()
                .placeHolder(R.drawable.ic_default_unit_num)
                .load();
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_contribute_history_hot_unit_num_history_item;
    }
}

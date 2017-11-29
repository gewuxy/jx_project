package jx.csp.adapter.me;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.me.HistoryVH;
import jx.csp.model.me.History;
import jx.csp.model.me.History.THistory;
import jx.csp.ui.activity.me.ContributeHistoryDetailActivityRouter;
import lib.ys.adapter.AdapterEx;

/**
 * @auther WangLan
 * @since 2017/10/13
 */

public class HistoryAdapter extends AdapterEx<History, HistoryVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_contribute_history_hkmovie_item;
    }

    @Override
    protected void refreshView(int position, HistoryVH holder) {
        History item = getItem(position);
        holder.getIvHead()
                .placeHolder(R.drawable.ic_default_unit_num_header)
                .url(item.getString(THistory.headimg))
                .load();
        holder.getTvName().setText(item.getString(THistory.acceptName));
        holder.getTvInfo().setText(item.getString(THistory.sign));
        setOnViewClickListener(position, holder.getItemLayout());
    }

    @Override
    protected void onViewClick(int position, View v) {
        History item = getItem(position);
        ContributeHistoryDetailActivityRouter.create(item.getInt(THistory.acceptId), item.getString(THistory.acceptName))
                .route(getContext());
    }
}

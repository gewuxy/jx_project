package jx.csp.adapter;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.FlowRateVH;
import jx.csp.model.FlowRate;
import jx.csp.model.FlowRate.TFlow;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * @auther Huoxuyu
 * @since 2017/12/13
 */

public class FlowRateAdapter extends RecyclerAdapterEx<FlowRate, FlowRateVH> {

    @Override
    protected void refreshView(int position, FlowRateVH holder) {
        FlowRate item = getItem(position);
        holder.getTvFlow().setText(item.getString(TFlow.flow).concat("G"));
        holder.getTvPrice().setText(item.getString(TFlow.price));
        holder.getTvCurrency().setText(item.getString(TFlow.currency));
        View layout = holder.getItemLayout();
        layout.setSelected(item.getBoolean(TFlow.select));
        setOnViewClickListener(position, layout);
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_flow_rate_price;
    }
}

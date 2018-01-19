package jx.csp.adapter;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.FlowRateVH;
import jx.csp.constant.LangType;
import jx.csp.model.FlowRate;
import jx.csp.model.FlowRate.TFlow;
import jx.csp.sp.SpApp;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.util.res.ResLoader;

/**
 * @auther HuoXuYu
 * @since 2017/12/13
 */

public class FlowRateAdapter extends RecyclerAdapterEx<FlowRate, FlowRateVH> {

    @Override
    protected void refreshView(int position, FlowRateVH holder) {
        FlowRate item = getItem(position);
        holder.getTvFlow().setText(item.getString(TFlow.flow).concat("G"));
        holder.getTvPrice().setText(item.getString(TFlow.price));
        holder.getTvCurrency().setText(item.getString(TFlow.currency));
        if (LangType.en == SpApp.inst().getLangType()) {
            goneView(holder.getTvPriceText());
        }
        View layout = holder.getItemLayout();
        layout.setSelected(item.getBoolean(TFlow.select));
        setOnViewClickListener(position, layout);
        if (item.getBoolean(TFlow.select)) {
            holder.getTvFlow().setTextColor(ResLoader.getColor(R.color.white));
            holder.getTvPrice().setTextColor(ResLoader.getColor(R.color.white));
            holder.getTvCurrency().setTextColor(ResLoader.getColor(R.color.white));
            holder.getTvPriceText().setTextColor(ResLoader.getColor(R.color.white));
        } else {
            holder.getTvFlow().setTextColor(ResLoader.getColor(R.color.text_333));
            holder.getTvPrice().setTextColor(ResLoader.getColor(R.color.text_1fbedd));
            holder.getTvCurrency().setTextColor(ResLoader.getColor(R.color.text_9699a2));
            holder.getTvPriceText().setTextColor(ResLoader.getColor(R.color.text_9699a2));
        }
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_flow_rate_price;
    }
}

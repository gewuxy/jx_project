package jx.csp.adapter;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.LiveFlowPriceVH;
import jx.csp.constant.LangType;
import jx.csp.model.LiveFlowPrice;
import jx.csp.model.LiveFlowPrice.TLiveFlowPrice;
import jx.csp.sp.SpApp;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.util.res.ResLoader;

/**
 * @auther HuoXuYu
 * @since 2017/12/13
 */

public class LiveFlowPriceAdapter extends RecyclerAdapterEx<LiveFlowPrice, LiveFlowPriceVH> {

    @Override
    protected void refreshView(int position, LiveFlowPriceVH holder) {
        LiveFlowPrice item = getItem(position);
        holder.getTvFlow().setText(item.getString(TLiveFlowPrice.flow).concat("G"));
        holder.getTvPrice().setText(item.getString(TLiveFlowPrice.price));
        holder.getTvCurrency().setText(item.getString(TLiveFlowPrice.currency));
        if (LangType.en == SpApp.inst().getLangType()) {
            goneView(holder.getTvPriceText());
        }
        View layout = holder.getItemLayout();
        layout.setSelected(item.getBoolean(TLiveFlowPrice.select));
        setOnViewClickListener(position, layout);
        if (item.getBoolean(TLiveFlowPrice.select)) {
            holder.getTvFlow().setTextColor(ResLoader.getColor(R.color.text_ace400));
            holder.getTvPrice().setTextColor(ResLoader.getColor(R.color.text_ace400));
            holder.getTvCurrency().setTextColor(ResLoader.getColor(R.color.text_ace400));
            holder.getTvPriceText().setTextColor(ResLoader.getColor(R.color.text_ace400));
        } else {
            holder.getTvFlow().setTextColor(ResLoader.getColor(R.color.text_9699a2));
            holder.getTvPrice().setTextColor(ResLoader.getColor(R.color.text_c6c5cb));
            holder.getTvCurrency().setTextColor(ResLoader.getColor(R.color.text_c6c5cb));
            holder.getTvPriceText().setTextColor(ResLoader.getColor(R.color.text_c6c5cb));
        }
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_live_flow_price;
    }
}

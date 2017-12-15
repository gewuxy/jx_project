package jx.csp.adapter.VH;

import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;

/**
 * @auther Huoxuyu
 * @since 2017/12/13
 */

public class FlowRateVH extends RecyclerViewHolderEx{

    public FlowRateVH(View itemView) {
        super(itemView);
    }

    public View getItemLayout(){
        return getView(R.id.flow_rate_layout_price);
    }

    public TextView getTvFlow(){
        return getView(R.id.flow_rate_tv_flow);
    }

    public TextView getTvPrice(){
        return getView(R.id.flow_rate_tv_price);
    }
    public TextView getTvPriceText(){
        return getView(R.id.flow_rate_tv_price_text);
    }

    public TextView getTvCurrency() {return getView(R.id.flow_rate_tv_currency);}
}

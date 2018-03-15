package jx.csp.adapter.VH;

import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;

/**
 * @auther HuoXuYu
 * @since 2017/12/13
 */

public class LiveFlowPriceVH extends RecyclerViewHolderEx{

    public LiveFlowPriceVH(View itemView) {
        super(itemView);
    }

    public View getItemLayout(){
        return getView(R.id.live_flow_layout_price);
    }

    public TextView getTvFlow(){
        return getView(R.id.live_flow_tv_flow);
    }

    public TextView getTvPrice(){
        return getView(R.id.live_flow_tv_price);
    }

    public TextView getTvPriceText(){
        return getView(R.id.live_flow_tv_price_text);
    }

    public TextView getTvCurrency() {return getView(R.id.live_flow_tv_currency);}
}

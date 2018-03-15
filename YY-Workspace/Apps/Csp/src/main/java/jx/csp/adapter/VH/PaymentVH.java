package jx.csp.adapter.VH;

import android.view.View;
import android.widget.ImageView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;

/**
 * @auther HuoXuYu
 * @since 2017/12/13
 */

public class PaymentVH extends RecyclerViewHolderEx{

    public PaymentVH(View itemView) {
        super(itemView);
    }

    public View getItemLayout() {return getView(R.id.flow_rate_layout_payment);}

    public ImageView getIvPayment() {return getView(R.id.flow_rate_iv_payment);}

    public View getItemSelectView() {return getView(R.id.live_flow_iv_select_state);}
}

package jx.csp.adapter;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.PaymentVH;
import jx.csp.model.Payment;
import jx.csp.model.Payment.TPayment;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * @auther Huoxuyu
 * @since 2017/12/13
 */

public class PaymentAdapter extends RecyclerAdapterEx<Payment, PaymentVH>{

    @Override
    protected void refreshView(int position, PaymentVH holder) {
        Payment item = getItem(position);
        holder.getIvPayment().setImageResource(item.getInt(TPayment.image));
        View layout = holder.getItemLayout();
        layout.setSelected(item.getBoolean(TPayment.select));
        setOnViewClickListener(position, layout);
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_flow_rate_payment;
    }
}
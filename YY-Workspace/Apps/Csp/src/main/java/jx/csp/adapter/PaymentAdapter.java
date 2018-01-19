package jx.csp.adapter;

import jx.csp.R;
import jx.csp.adapter.VH.PaymentVH;
import jx.csp.model.Payment;
import jx.csp.model.Payment.TPayment;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * @auther HuoXuYu
 * @since 2017/12/13
 */

public class PaymentAdapter extends RecyclerAdapterEx<Payment, PaymentVH>{

    @Override
    protected void refreshView(int position, PaymentVH holder) {
        Payment item = getItem(position);
        holder.getIvPayment().setImageResource(item.getInt(TPayment.image));
        holder.getItemView().setSelected(item.getBoolean(TPayment.select));
        setOnViewClickListener(position, holder.getItemLayout());
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_flow_rate_payment;
    }
}

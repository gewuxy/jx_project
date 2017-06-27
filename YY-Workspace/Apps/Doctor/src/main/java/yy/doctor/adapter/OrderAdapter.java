package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import yy.doctor.R;
import yy.doctor.adapter.VH.OrderVH;
import yy.doctor.model.me.Order;
import yy.doctor.model.me.Order.TOrder;

import static lib.ys.util.res.ResLoader.getString;

/**
 * 订单的adapter
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class OrderAdapter extends AdapterEx<Order, OrderVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_order_item;
    }

    @Override
    protected void refreshView(int position, OrderVH holder) {

        Order item = getItem(position);

        holder.getTvName().setText(item.getString(TOrder.name));
        holder.getTvOrderNum().setText(item.getString(TOrder.orderNo));

        int state = item.getInt(TOrder.status);
        String str = null;
        switch (state) {
            case 0: {
                str = getString(R.string.order_state_zero);
            }
            break;
            case 1: {
                str = getString(R.string.order_state_one);
            }
            break;
            case 2: {
                str = getString(R.string.order_state_two);
            }
            break;
            case 3: {
                str = getString(R.string.order_state_three);
            }
            break;
        }
        holder.getTvStatus().setText(str);

        String strTime = TimeUtil.formatMilli(item.getLong(TOrder.createTime), TimeFormat.from_y_24);
        holder.getTvTime().setText(strTime);

        holder.getTvPayEpn().setText(item.getString(TOrder.price) + getString(R.string.epn));
        holder.getTvAdress().setText(item.getString(TOrder.province) + item.getString(TOrder.address));
        holder.getTvMobile().setText(item.getString(TOrder.phone));
        holder.getTvReceiver().setText(item.getString(TOrder.receiver));
        holder.getTvOrderInfo().setText(item.getString(TOrder.postUnit));
    }

}

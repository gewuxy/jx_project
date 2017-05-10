package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import yy.doctor.R;
import yy.doctor.adapter.VH.OrderVH;
import yy.doctor.model.me.Order;
import yy.doctor.model.me.Order.TOrder;

/**
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

        List<Order> list = getData();
        Order item = list.get(position);

        holder.getTvName().setText(item.getString(TOrder.name));
        //holder.getTvNum().setText();
        holder.getTvOrderNum().setText(item.getString(TOrder.id));
        //holder.getTvStatus().setText();
        String strTime = TimeUtil.formatMilli(item.getLong(TOrder.createTime), TimeFormat.from_y_24);
        holder.getTvTime().setText(strTime);
        //holder.getTvAdress().setText();
        holder.getTvMobile().setText(item.getString(TOrder.phone));
        holder.getTvReceiver().setText(item.getString(TOrder.receiver));
        holder.getTvOrderInfo().setText(item.getString(TOrder.postUnit));

    }

}

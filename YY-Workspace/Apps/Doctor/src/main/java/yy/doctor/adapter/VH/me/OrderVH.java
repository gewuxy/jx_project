package yy.doctor.adapter.VH.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class OrderVH extends ViewHolderEx {

    public OrderVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvName() {
        return getView(R.id.order_item_tv_name);
    }

    public TextView getTvNum() {
        return getView(R.id.order_item_tv_num);
    }

    public TextView getTvOrderNum() {
        return getView(R.id.order_item_tv_order_num);
    }

    public TextView getTvStatus() {
        return getView(R.id.order_item_tv_status);
    }

    public TextView getTvTime() {
        return getView(R.id.order_item_tv_time);
    }

    public TextView getTvPayEpn() {
        return getView(R.id.order_item_tv_pay_epn);
    }

    public TextView getTvAdress() {
        return getView(R.id.order_item_tv_address);
    }

    public TextView getTvMobile() {
        return getView(R.id.order_item_tv_mobile);
    }

    public TextView getTvReceiver() {
        return getView(R.id.order_item_tv_receiver);
    }

    public TextView getTvOrderInfo() {
        return getView(R.id.order_item_tv_order_info);
    }

}

package jx.doctor.model.me;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import jx.doctor.model.me.Order.TOrder;

/**
 * @author CaiXiang
 * @since 2017/5/6
 */
public class Order extends EVal<TOrder> {

    @IntDef({
            OrderState.pending,
            OrderState.accepted_order,
            OrderState.shipped,
            OrderState.received,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrderState {
        int pending = 0;  // 待处理
        int accepted_order = 1; // 已接受订单
        int shipped = 2; // 已发货
        int received = 3; // 已签收
    }

    public enum TOrder {

        id,    //订单id
        orderNo,    //订单号
        receiver,    //收件人
        createTime,    //下单时间
        phone,    //手机号
        zoneCode,    //邮政编码

        /**
         * {@link OrderState}
         */
        status,    //订单状态  0表示待处理 1表示已接受订单 2表示已发货 3表示商品已接收
        postNo,    //物流单号
        postUnit,    //物流单位
        postType,    //配送方式
        name,    //商品名字
        price,    //商品价格
        address,  //地址
        province,  //省市
    }
}

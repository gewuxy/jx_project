package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.Order.TOrder;

/**
 * @author CaiXiang
 * @since 2017/5/6
 */
public class Order extends EVal<TOrder> {

    public enum TOrder {

        id,    //订单id
        orderNo,    //订单号
        receiver,    //收件人
        createTime,    //下单时间
        phone,    //手机号
        zoneCode,    //邮政编码
        status,    //订单状态
        postNo,    //物流单号
        postUnit,    //物流单位
        postType,    //配送方式
        name,    //商品名字
        price,    //商品价格

    }
}

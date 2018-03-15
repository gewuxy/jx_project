package jx.csp.model;

import jx.csp.model.LiveFlowPrice.TLiveFlowPrice;
import lib.ys.model.EVal;

/**
 * 流量管理(本地字段)
 *
 * @auther HuoXuYu
 * @since 2017/12/13
 */

public class LiveFlowPrice extends EVal<TLiveFlowPrice> {

    public LiveFlowPrice(String flow, String price, String currency, boolean select) {
        put(TLiveFlowPrice.flow, flow);
        put(TLiveFlowPrice.price, price);
        put(TLiveFlowPrice.currency, currency);
        put(TLiveFlowPrice.select, select);
    }

    public enum TLiveFlowPrice {
        flow, // 数量
        price, // 价格
        currency, // 币种
        select, // 高亮
    }
}

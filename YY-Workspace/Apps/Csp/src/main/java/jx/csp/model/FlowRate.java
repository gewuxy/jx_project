package jx.csp.model;

import jx.csp.model.FlowRate.TFlow;
import lib.ys.model.EVal;

/**
 * 流量管理(本地字段)
 *
 * @auther Huoxuyu
 * @since 2017/12/13
 */

public class FlowRate extends EVal<TFlow> {

    public FlowRate(String flow, String price, String currency, boolean select) {
        put(TFlow.flow, flow);
        put(TFlow.price, price);
        put(TFlow.currency, currency);
        put(TFlow.select, select);
    }

    public enum TFlow {
        flow, // 数量
        price, // 价格
        currency, // 币种
        select, // 高亮
    }
}

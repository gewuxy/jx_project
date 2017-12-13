package jx.csp.model;

import jx.csp.model.Payment.TPayment;
import lib.ys.model.EVal;

/**
 * 流量管理的支付方式(本地)
 *
 * @auther Huoxuyu
 * @since 2017/12/13
 */

public class Payment extends EVal<TPayment>{

    public enum TPayment {
        id,
        image,
        select,
    }
}

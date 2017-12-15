package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 流量支付方式id
 *
 * @auther Huoxuyu
 * @since 2017/12/14
 */

@IntDef({
        PayType.alipay,
        PayType.wechat,
        PayType.unionpay,
        PayType.paypal
})
@Retention(RetentionPolicy.SOURCE)
public @interface PayType {
    int alipay = 0;
    int wechat = 1;
    int unionpay = 2;
    int paypal = 3;
}

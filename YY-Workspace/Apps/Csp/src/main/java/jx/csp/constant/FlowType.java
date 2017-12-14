package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther Huoxuyu
 * @since 2017/12/14
 */

@IntDef({
        FlowType.alipay,
        FlowType.wechat,
        FlowType.unionpay,
        FlowType.paypal
})
@Retention(RetentionPolicy.SOURCE)
public @interface FlowType {
    int alipay = 0;
    int wechat = 1;
    int unionpay = 2;
    int paypal = 3;
}

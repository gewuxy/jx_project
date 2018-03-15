package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 提现类型
 *
 * @auther : GuoXuan
 * @since : 2018/3/12
 */
@IntDef({
        InvoiceType.electronic,
        InvoiceType.paper,
})
@Retention(RetentionPolicy.SOURCE)
public @interface InvoiceType {
    int electronic = 0; // 0电子发票
    int paper = 1; // 1纸质发票
}

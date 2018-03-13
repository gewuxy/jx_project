package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 稿件状态
 *
 * @auther : GuoXuan
 * @since : 2018/3/12
 */
@IntDef({
        RoyaltyType.get_royalty,
        RoyaltyType.check,
        RoyaltyType.succeed,
        RoyaltyType.reject,
})
@Retention(RetentionPolicy.SOURCE)
public @interface RoyaltyType {
    int get_royalty = 0; // 0代表收稿方已支付稿酬
    int check = 1; // 1表示提现审核中
    int succeed = 2; // 2表示成功提现
    int reject = 3; // 3表示提现被拒绝
}

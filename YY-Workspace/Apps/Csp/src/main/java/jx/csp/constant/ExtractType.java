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
        ExtractType.company,
        ExtractType.person,
})
@Retention(RetentionPolicy.SOURCE)
public @interface ExtractType {
    int company = 0; // 0代表企业提现
    int person = 1; // 1表示个人提现
}

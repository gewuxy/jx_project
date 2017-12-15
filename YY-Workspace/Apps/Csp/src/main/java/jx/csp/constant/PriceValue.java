package jx.csp.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 流量金额
 *
 * @auther Huoxuyu
 * @since 2017/12/14
 */

@StringDef({
        PriceValue.flow1,
        PriceValue.flow2,
        PriceValue.flow3,
        PriceValue.flow4,

        PriceValue.cnyPrice1,
        PriceValue.cnyPrice2,
        PriceValue.cnyPrice3,
        PriceValue.cnyPrice4,

        PriceValue.usdPrice1,
        PriceValue.usdPrice2,
        PriceValue.usdPrice3,
        PriceValue.usdPrice4,
})

@Retention(RetentionPolicy.SOURCE)
public @interface PriceValue {
    /**
     * 流量值
     */
    String flow1 = "5";
    String flow2 = "25";
    String flow3 = "100";
    String flow4 = "500";

    /**
     * 人民币售价
     */
    String cnyPrice1 = "10";
    String cnyPrice2 = "50";
    String cnyPrice3 = "200";
    String cnyPrice4 = "1000";

    /**
     * 美元售价
     */
    String usdPrice1 = "1.75";
    String usdPrice2 = "8.75";
    String usdPrice3 = "35";
    String usdPrice4 = "175";
}

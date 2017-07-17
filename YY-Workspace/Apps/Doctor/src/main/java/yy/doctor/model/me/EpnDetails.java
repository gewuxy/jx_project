package yy.doctor.model.me;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import yy.doctor.model.me.EpnDetails.TEpnDetails;

/**
 * @author CaiXiang
 * @since 2017/5/5
 */
public class EpnDetails extends EVal<TEpnDetails> {

    @IntDef({
            EpnDetailType.pay,
            EpnDetailType.recharge,
            EpnDetailType.award
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface EpnDetailType {
        int pay = 0;  // 表示支付
        int recharge = 1; // 1表示充值
        int award = 2;  // 2表示获得
    }

    public enum TEpnDetails {

        cost,
        costTime,
        oppositeName,
        description,

        /**
         * {@link EpnDetailType}
         */
        type,  // 0表示支付 1表示充值 2表示获得
    }

}

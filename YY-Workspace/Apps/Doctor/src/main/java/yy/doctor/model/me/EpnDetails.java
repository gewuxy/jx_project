package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.EpnDetails.TEpnDetails;

/**
 * @author CaiXiang
 * @since 2017/5/5
 */
public class EpnDetails extends EVal<TEpnDetails> {

    public enum TEpnDetails {

        cost,
        costTime,
        oppositeName,
        description,
        type,  // 0表示支付 1表示充值 2表示获得
    }

}

package pay;

import lib.ys.model.EVal;
import pay.PayResult.TPayResult;

/**
 * @auther yuansui
 * @since 2017/10/11
 */

public class PayResult extends EVal<TPayResult> {

    public enum TPayResult {
        /**
         * {@link pay.PayAction.PayType}
         */
        type,

        requestCode,
        resultCode,
        data,

    }
}

package pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pay.PayResult.TPayResult;

/**
 * @auther yuansui
 * @since 2017/10/11
 */

public class PayAction {

    @IntDef({
            PayType.unknown,
            PayType.pingPP,
            PayType.payPal,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PayType {
        int unknown = -1;
        int pingPP = 0;
        int payPal = 1;
    }

    public static void pingPay(Activity activity, String info) {
        PingPay.pay(activity, info);
    }

    public static void startPayPalService(Context context){
        PayPalPay.startPayPalService(context);
    }

    public static void stopPayPalService(Context context){
        PayPalPay.stopPayPalService(context);
    }

    public static void payPalPay(Context context, int flow){
        PayPalPay.onPayPalPay(context, flow);
    }


    public static void onResult(PayResult result, OnPayListener listener) {
        switch (result.getInt(TPayResult.type)) {
            case PayType.pingPP: {
                PingPay.onResult(result.getInt(TPayResult.requestCode), result.getInt(TPayResult.resultCode), (Intent) result.getObject(TPayResult.data), listener);
            }
            break;
            case PayType.payPal: {
                PayPalPay.onResult(result.getInt(TPayResult.requestCode), result.getInt(TPayResult.resultCode), (Intent) result.getObject(TPayResult.data), listener);
            }
            break;
            default: {
                // 未知支付方式
            }
        }
    }

}

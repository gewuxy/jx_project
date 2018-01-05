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

    public static void payPalPay(Context context, String money){
        PayPalPay.onPayPalPay(context, money);
    }


    public static void onResult(String url, PayResult result, OnPayListener listener) {
        int requestCode = result.getInt(TPayResult.requestCode);
        int resultCode = result.getInt(TPayResult.resultCode);
        Intent data = (Intent) result.getObject(TPayResult.data);
        switch (result.getInt(TPayResult.type)) {
            case PayType.pingPP: {
                PingPay.onResult(requestCode, resultCode, data, listener);
            }
            break;
            case PayType.payPal: {
                PayPalPay.onResult(requestCode, resultCode, data, listener, url);
            }
            break;
            default: {
                // 未知支付方式
            }
        }
    }

}

package pay;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.StringDef;

import com.pingplusplus.android.Pingpp;

import lib.ys.YSLog;

/**
 * Ping++
 *
 * @auther yuansui
 * @since 2017/10/11
 */

public class PingPay {

    private static final String TAG = PingPay.class.getSimpleName();

    @StringDef({
            PingPayChannel.alipay,
            PingPayChannel.wechat,
            PingPayChannel.upacp,
    })
    public @interface PingPayChannel {
        String alipay = "alipay"; // 支付宝
        String wechat = "wx"; // 微信
        String upacp = "upacp"; // 银联
    }

    /**
     * 调用Ping++支付
     *
     * @param activity
     * @param chargeInfo
     */
    public static void pay(Activity activity, String chargeInfo) {
        Pingpp.createPayment(activity, chargeInfo);
    }

    public static void onResult(int requestCode, int resultCode, Intent data, OnPayListener listener) {
        // ping++支付场景
        if (resultCode == Activity.RESULT_OK) {
            String result = data.getExtras().getString("pay_result");
            /**
             *  处理返回值
             * "success" - 支付成功
             * "fail"    - 支付失败
             * "cancel"  - 取消支付
             * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
             */
            String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
            String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
            YSLog.d(TAG, "onResultData: err msg = " + errorMsg);
            YSLog.d(TAG, "onResultData: extra msg = " + extraMsg);

            if (result.equals("success")) {
                listener.onPaySuccess();
            } else if (result.equals("fail")) {
                listener.onPayError("支付失败");
            } else if (result.equals("cancel")) {
                listener.onPayError("取消支付");
            } else if (result.equals("invalid")) {
                listener.onPayError("支付插件未安装");
            }
        }
    }
}

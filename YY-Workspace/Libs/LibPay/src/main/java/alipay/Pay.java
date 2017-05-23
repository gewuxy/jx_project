package alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;

/**
 * 支付宝支付接口
 *
 * @author CaiXiang
 * @since 2017/5/19
 */

public class Pay {

    public static void aliPay(final Activity activity, final String orderInfo, final Handler handler) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask aliPay = new PayTask(activity);
                String result = aliPay.pay(orderInfo, true);
                Message msg = new Message();
                msg.what = 10;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };
        // 必须异步调用
        new Thread(payRunnable).start();
    }

}

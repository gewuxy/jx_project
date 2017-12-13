package pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import lib.network.Network;
import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.network.model.interfaces.OnNetworkListener;
import lib.jx.network.BaseJsonParser;

/**
 * PayPal
 *
 * @auther Huoxuyu
 * @since 2017/10/12
 */

public class PayPalPay {

    public static final String KExtraOrderId = "order_id";

    // PayPal真实环境
    private static final String KConfigEnvironment = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    // paypal申请到的设备ID
    private static final String KConfigClientId = "ATVIu3HZHJ7ZVWPHPsjKbh7hJzduksrdYxwEQgdzibusZfFO2Cy8s4c0MU5f-XUnENZafVuVNINfMTqK";

    // PayPal沙盒测试
//    private static final String KConfigEnvironment = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    //paypal官方申请的沙盒设备ID
//    private static final String KConfigClientId = "ATBGjclV9GcygEwPz_58PUlxOvh0sJvC_Md3ZuTghMGlGIfQzgID_2zh93Ku44nMV6bcuGyoDvN3GHKv";

    private static PayPalConfiguration mConfig = new PayPalConfiguration()
            .environment(KConfigEnvironment)
            .clientId(KConfigClientId);

    private static final int KRequestCodePayment = 1;

    private static String mPaymentId;

    /**
     * 启动PayPal服务
     *
     * @param context
     */
    public static void startPayPalService(Context context) {
        Intent intent = new Intent(context, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mConfig);
        context.startService(intent);
    }

    /**
     * 停止PayPal服务
     *
     * @param context
     */
    public static void stopPayPalService(Context context) {
        context.stopService(new Intent(context, PayPalService.class));
    }

    /**
     * 开始执行支付操作
     *
     * @param context
     * @param flow
     */
    public static void onPayPalPay(Context context, int flow) {
        String money = null;
        switch (flow) {
            case 5: {
                money = "1.75";
            }
            break;
            case 25: {
                money = "8.75";
            }
            break;
            case 100: {
                money = "35";
            }
            break;
            case 500: {
                money = "175";
            }
            break;
        }
        PayPalPayment payment = new PayPalPayment(new BigDecimal(money), "USD", "money", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(context, PaymentActivity.class);
        //发送相同的配置以恢复弹性
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        ((Activity) context).startActivityForResult(intent, KRequestCodePayment);
    }

    public static void onResult(int requestCode, int resultCode, Intent data, OnPayListener listener) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            try {
                mPaymentId = confirm.toJSONObject().getJSONObject("response").getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (confirm != null) {
                Network network = new Network("PayPal", null);
                network.load(0,
                        NetworkReq.newBuilder("http://59.111.90.245:8084/api/charge/paypalCallback")
                                .post()
                                .param("paymentId", mPaymentId)
                                .param("orderId", data.getStringExtra(KExtraOrderId))
                                .build(),
                        new OnNetworkListener() {

                            @Override
                            public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
                                return BaseJsonParser.error(resp.getText());
                            }

                            @Override
                            public void onNetworkSuccess(int id, IResult r) {
                                if (r.isSucceed()) {
                                    listener.onPaySuccess();
                                } else {
                                    onNetworkError(id, r.getError());
                                }
                            }

                            @Override
                            public void onNetworkError(int id, NetworkError error) {
                                listener.onPayError("支付失败");
                            }

                            @Override
                            public void onNetworkProgress(int id, float progress, long totalSize) {

                            }
                        });

            }
        } else {
            listener.onPayError("支付失败");
        }
    }
}

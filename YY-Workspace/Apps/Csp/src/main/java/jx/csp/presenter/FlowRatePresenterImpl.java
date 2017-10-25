package jx.csp.presenter;

import jx.csp.contact.FlowRateContract;
import jx.csp.model.pay.PayPalPayRecharge;
import jx.csp.model.pay.PayPalPayRecharge.TPayPalPayRecharge;
import jx.csp.model.pay.PingPayRecharge;
import jx.csp.model.pay.PingPayRecharge.TPingPayRecharge;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.PayAPI;
import lib.network.model.NetworkResp;
import lib.yy.network.Result;

/**
 * @auther yuansui
 * @since 2017/10/24
 */

public class FlowRatePresenterImpl extends PresenterExImpl<FlowRateContract.V> implements FlowRateContract.P{

    private final int KPayPalPayCode = 1;

    private FlowRateContract.V mView;

    public FlowRatePresenterImpl(FlowRateContract.V v) {
        super(v);
        mView = v;
    }

    @Override
    public void getDataFormNet(int id, int flow, String channel) {
        if (id == KPayPalPayCode) {
            exeNetworkReq(KPayPalPayCode, PayAPI.paypalPay(flow).build());
        }else {
            exeNetworkReq(PayAPI.pingPay(flow, channel).build());
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KPayPalPayCode) {
            return JsonParser.ev(r.getText(), PayPalPayRecharge.class);
        } else {
            return JsonParser.ev(r.getText(), PingPayRecharge.class);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KPayPalPayCode) {
            Result<PayPalPayRecharge> r = (Result<PayPalPayRecharge>) result;
            if (r.isSucceed()) {
                PayPalPayRecharge recharge = r.getData();
                String orderId = recharge.getString(TPayPalPayRecharge.orderId);

                mView.payPalPay(orderId);
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            Result<PingPayRecharge> r = (Result<PingPayRecharge>) result;
            if (r.isSucceed()) {
                PingPayRecharge recharge = r.getData();
                String charge = recharge.getString(TPingPayRecharge.charge);

                mView.pingPay(charge);
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }
}

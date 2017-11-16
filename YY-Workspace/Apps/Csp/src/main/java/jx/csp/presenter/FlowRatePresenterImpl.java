package jx.csp.presenter;

import jx.csp.contact.FlowRateContract;
import jx.csp.model.pay.PayPalPayRecharge;
import jx.csp.model.pay.PayPalPayRecharge.TPayPalPayRecharge;
import jx.csp.model.pay.PingPayRecharge;
import jx.csp.model.pay.PingPayRecharge.TPingPayRecharge;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.PayAPI;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther yuansui
 * @since 2017/10/24
 */

public class FlowRatePresenterImpl extends BasePresenterImpl<FlowRateContract.V> implements FlowRateContract.P {

    private final int KPayPalPayCode = 1;

    public FlowRatePresenterImpl(FlowRateContract.V v) {
        super(v);
    }

    @Override
    public void confirmPay(int id, int flow, String channel) {
        if (id == KPayPalPayCode) {
            exeNetworkReq(KPayPalPayCode, PayAPI.paypalPay(flow).build());
        } else {
            exeNetworkReq(PayAPI.pingPay(flow, channel).build());
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KPayPalPayCode) {
            return JsonParser.ev(resp.getText(), PayPalPayRecharge.class);
        } else {
            return JsonParser.ev(resp.getText(), PingPayRecharge.class);
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (!r.isSucceed()) {
            onNetworkError(id, r.getError());
            return;
        }
        if (id == KPayPalPayCode) {
            PayPalPayRecharge recharge = (PayPalPayRecharge) r.getData();
            String orderId = recharge.getString(TPayPalPayRecharge.orderId);

            getView().setPayPalPay(orderId);
        } else {
            PingPayRecharge recharge = (PingPayRecharge) r.getData();
            String charge = recharge.getString(TPingPayRecharge.charge);

            getView().setPingPay(charge);
        }
    }
}

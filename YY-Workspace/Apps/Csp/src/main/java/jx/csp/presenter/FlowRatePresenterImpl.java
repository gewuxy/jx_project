package jx.csp.presenter;

import android.content.Intent;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.FlowRateContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.pay.PayPalPayRecharge;
import jx.csp.model.pay.PayPalPayRecharge.TPayPalPayRecharge;
import jx.csp.model.pay.PingPayRecharge;
import jx.csp.model.pay.PingPayRecharge.TPingPayRecharge;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.PayAPI;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.yy.contract.BasePresenterImpl;
import pay.OnPayListener;
import pay.PayAction;
import pay.PayAction.PayType;
import pay.PayPalPay;
import pay.PayResult;
import pay.PayResult.TPayResult;

/**
 * @auther yuansui
 * @since 2017/10/24
 */

public class FlowRatePresenterImpl extends BasePresenterImpl<FlowRateContract.V> implements FlowRateContract.P {

    private final int KFlowConversion = 1024; //流量单位
    private final int KPingReqCode = 0;
    private final int KPayPalPayCode = 1;

    private String mOrderId;
    private int mFlow;

    public FlowRatePresenterImpl(FlowRateContract.V v) {
        super(v);
    }

    @Override
    public void confirmPay(int id, int flow, String channel) {
        mFlow = flow;
        if (id == KPayPalPayCode) {
            exeNetworkReq(KPayPalPayCode, PayAPI.paypalPay(mFlow).build());
        } else {
            exeNetworkReq(PayAPI.pingPay(mFlow, channel).build());
        }
    }

    @Override
    public void setResultDeal(int code, int requestCode, int resultCode, Intent data) {
        final PayResult payResult = new PayResult();

        if (code == KPingReqCode) {
            payResult.put(TPayResult.type, PayType.pingPP);
        } else if (code == KPayPalPayCode) {
            payResult.put(TPayResult.type, PayType.payPal);
            if (data != null) {
                data.putExtra(PayPalPay.KExtraOrderId, mOrderId);
            } else {
                App.showToast(R.string.flow_rate_pay_fail);
                return;
            }
        }
        payResult.put(TPayResult.requestCode, requestCode);
        payResult.put(TPayResult.resultCode, resultCode);
        payResult.put(TPayResult.data, data);

        PayAction.onResult(payResult, new OnPayListener() {

            @Override
            public void onPaySuccess() {
                Profile.inst().increase(TProfile.flux, mFlow * KFlowConversion);
                Profile.inst().saveToSp();

                getView().setSurplusFlowRate();
                App.showToast(R.string.flow_rate_pay_success);
            }

            @Override
            public void onPayError(String error) {
                App.showToast(R.string.flow_rate_pay_fail);
            }
        });
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
            mOrderId = recharge.getString(TPayPalPayRecharge.orderId);

            getView().setPayPalPay();
        } else {
            PingPayRecharge recharge = (PingPayRecharge) r.getData();
            String charge = recharge.getString(TPingPayRecharge.charge);

            getView().setPingPay(charge);
        }
    }
}

package jx.csp.ui.activity.me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.pay.PayPalPayRecharge;
import jx.csp.model.pay.PayPalPayRecharge.TPayPalPayRecharge;
import jx.csp.model.pay.PingPayRecharge;
import jx.csp.model.pay.PingPayRecharge.TPingPayRecharge;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.PayAPI;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.model.MapList;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import pay.OnPayListener;
import pay.PayAction;
import pay.PayAction.PayType;
import pay.PayPalPay;
import pay.PayResult;
import pay.PayResult.TPayResult;
import pay.PingPay.PingPayChannel;

/**
 * 流量管理
 *
 * @auther Huoxuyu
 * @since 2017/10/9
 */

public class FlowRateManageActivity extends BaseActivity {

    private final String KSurplusFlowUnit = "G";

    private final int KPingReqCode = 0;
    private final int KPayPalPayCode = 1;

    private EditText mEtRecharge;

    private TextView mTvSurplus;
    private TextView mTvUnit;
    private TextView mTvMoney;
    private TextView mTvPay;

    private MapList<Integer, View> mChannelViews;
    private View mPreChannelView;

    private String mOrderId;
    private String mEtText;
    private int mRechargeSum;
    private int mReqCode;//识别号，区分支付方式

    @Override
    public void initData() {
        mChannelViews = new MapList<>();

        PayAction.startPayPalService(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_flow_rate_manage;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getString(R.string.flow_rate_manage), this);
    }

    @Override
    public void findViews() {
        mEtRecharge = findView(R.id.flow_rate_et_recharge);

        mTvSurplus = findView(R.id.flow_rate_tv_surplus);
        mTvUnit = findView(R.id.flow_rate_tv_unit);
        mTvMoney = findView(R.id.flow_rate_tv_money);
        mTvPay = findView(R.id.flow_rate_tv_pay);

        findChannelView(R.id.flow_rate_iv_alipay);
        findChannelView(R.id.flow_rate_iv_wechat);
        findChannelView(R.id.flow_rate_iv_unionpay);
        findChannelView(R.id.flow_rate_iv_paypal);
    }

    @Override
    public void setViews() {
        // 初始化高亮第一个
        mPreChannelView = mChannelViews.get(0);
        mPreChannelView.setSelected(true);

        mTvPay.setEnabled(false);
        mTvSurplus.setText(Profile.inst().getInt(TProfile.flux) / 1024 + KSurplusFlowUnit);

        setOnClickListener(R.id.flow_rate_tv_pay);
        for (View v : mChannelViews) {
            setOnClickListener(v);
        }

        mEtRecharge.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int editStart = mEtRecharge.getSelectionStart();
                int editEnd = mEtRecharge.getSelectionEnd();

                mEtRecharge.removeTextChangedListener(this);

                Pattern pattern = Pattern.compile("0[0-9]");
                Matcher matcher = pattern.matcher(s.toString());
                // 如果是以0开头截断后面的字符串
                if (matcher.matches()) {
                    s.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
                mEtRecharge.setSelection(editStart);
                mEtText = mEtRecharge.getText().toString().trim();
                String num = String.format(getString(R.string.flow_rate_amount), TextUtil.isEmpty(mEtText) ? 0 : Integer.valueOf(mEtText) * 2);

                mTvMoney.setText(num);
                mEtRecharge.addTextChangedListener(this);

                if (TextUtil.isNotEmpty(mEtText)) {
                    mTvPay.setEnabled(true);
                    ViewUtil.showView(mTvUnit);
                } else {
                    mTvPay.setEnabled(false);
                    ViewUtil.goneView(mTvUnit);
                }
            }
        });
    }

    private void findChannelView(int id) {
        mChannelViews.add(Integer.valueOf(id), findView(id));
    }

    private void changeChannelFocus(View v) {
        if (mPreChannelView.equals(v)) {
            return;
        }
        mPreChannelView.setSelected(false);
        v.setSelected(true);
        mPreChannelView = v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flow_rate_iv_unionpay:
            case R.id.flow_rate_iv_paypal:
            case R.id.flow_rate_iv_wechat:
            case R.id.flow_rate_iv_alipay: {
                changeChannelFocus(mChannelViews.getByKey(v.getId()));
            }
            break;
            case R.id.flow_rate_tv_pay: {
                mRechargeSum = Integer.valueOf(mEtText);
                if (mRechargeSum == 0) {
                    showToast(R.string.flow_rate_input_top_up);
                    return;
                }

                // 1) 请求服务器获取charge
                switch (mPreChannelView.getId()) {
                    case R.id.flow_rate_iv_alipay: {
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(PayAPI.pingPay(mRechargeSum, PingPayChannel.alipay).build());
                    }
                    break;
                    case R.id.flow_rate_iv_wechat: {
                        // FIXME: 2017/10/16 微信支付未注册
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(PayAPI.pingPay(mRechargeSum, PingPayChannel.wechat).build());
                    }
                    break;
                    case R.id.flow_rate_iv_unionpay: {
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(PayAPI.pingPay(mRechargeSum, PingPayChannel.upacp).build());
                    }
                    break;
                    case R.id.flow_rate_iv_paypal: {
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(KPayPalPayCode, PayAPI.paypalPay(mRechargeSum).build());
                    }
                    break;
                }
            }
            break;
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
                stopRefresh();

                mReqCode = KPayPalPayCode;
                mOrderId = r.getData().getString(TPayPalPayRecharge.orderId);
                PayAction.payPalPay(this, String.valueOf(mRechargeSum));

            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            Result<PingPayRecharge> r = (Result<PingPayRecharge>) result;
            if (r.isSucceed()) {
                stopRefresh();

                PingPayRecharge recharge = r.getData();
                String charge = recharge.getString(TPingPayRecharge.charge);
                mReqCode = KPingReqCode;
                PayAction.pingPay(this, charge);
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final PayResult payResult = new PayResult();

        if (mReqCode == KPingReqCode) {
            payResult.put(TPayResult.type, PayType.pingPP);
        } else if (mReqCode == KPayPalPayCode) {
            payResult.put(TPayResult.type, PayType.payPal);
            data.putExtra(PayPalPay.KExtraOrderId, mOrderId);
        }
        payResult.put(TPayResult.requestCode, requestCode);
        payResult.put(TPayResult.resultCode, resultCode);
        payResult.put(TPayResult.data, data);

        PayAction.onResult(payResult, new OnPayListener() {

            @Override
            public void onPaySuccess() {
                int flowRate = Profile.inst().getInt(TProfile.flux) + mRechargeSum * 1024;

                Profile.inst().put(TProfile.flux, flowRate);
                Profile.inst().saveToSp();

                mTvSurplus.setText(flowRate / 1024 + KSurplusFlowUnit);
                showToast(R.string.flow_rate_pay_success);
            }

            @Override
            public void onPayError(String error) {
                showToast(R.string.flow_rate_pay_fail);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PayAction.stopPayPalService(this);
    }
}

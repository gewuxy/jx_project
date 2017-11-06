package jx.csp.ui.activity.me;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jx.csp.R;
import jx.csp.contact.FlowRateContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.presenter.FlowRatePresenterImpl;
import jx.csp.util.Util;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.model.MapList;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
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
    private final int KFlowConversion = 1024;

    private final int KPingReqCode = 0;
    private final int KPayPalPayCode = 1;

    private EditText mEtFlowRate;

    private TextView mTvSurplus;
    private TextView mTvUnit;
    private TextView mTvMoney;
    private TextView mTvPay;

    private MapList<Integer, View> mChannelViews;
    private View mPreChannelView;

    private String mOrderId;
    private String mFlowRate;
    private int mRechargeSum;
    private int mReqCode;//识别号，区分支付方式

    private FlowRateContract.P mPresenter;
    private FlowRateContract.V mView;


    @Override
    public void initData() {
        mChannelViews = new MapList<>();

        PayAction.startPayPalService(this);

        mView = new FlowRateViewImpl();
        mPresenter = new FlowRatePresenterImpl(mView);
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
        mEtFlowRate = findView(R.id.flow_rate_et_recharge);

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
        setOnClickListener(R.id.flow_rate_tv_pay);
        for (View v : mChannelViews) {
            setOnClickListener(v);
        }

        // 初始化高亮第一个
        mView.setHighlight(mChannelViews.get(0).getId());
        mView.setPayStatus();
        mView.setSurplusFlowRate();

        mEtFlowRate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mView.setInputRule();
                mView.setActualPaymentMoney();
                mView.setPayStatus();
            }
        });
    }

    private void findChannelView(int id) {
        mChannelViews.add(Integer.valueOf(id), findView(id));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flow_rate_iv_unionpay:
            case R.id.flow_rate_iv_paypal:
            case R.id.flow_rate_iv_wechat:
            case R.id.flow_rate_iv_alipay: {
                mView.setHighlight(v.getId());
            }
            break;
            case R.id.flow_rate_tv_pay: {
                mRechargeSum = Integer.valueOf(mFlowRate);
                if (mRechargeSum == 0) {
                    showToast(R.string.flow_rate_input_top_up);
                    return;
                }
                // 1) 请求服务器获取charge
                refresh(RefreshWay.dialog);
                switch (mPreChannelView.getId()) {
                    case R.id.flow_rate_iv_alipay: {
                        mPresenter.confirmPay(KPingReqCode, mRechargeSum, PingPayChannel.alipay);
                    }
                    break;
                    case R.id.flow_rate_iv_wechat: {
                        mPresenter.confirmPay(KPingReqCode, mRechargeSum, PingPayChannel.wechat);
                    }
                    break;
                    case R.id.flow_rate_iv_unionpay: {
                        mPresenter.confirmPay(KPingReqCode, mRechargeSum, PingPayChannel.upacp);
                    }
                    break;
                    case R.id.flow_rate_iv_paypal: {
                        mPresenter.confirmPay(KPayPalPayCode, mRechargeSum, null);
                    }
                    break;
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mView.setResultDeal(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayAction.stopPayPalService(this);
    }

    private class FlowRateViewImpl implements FlowRateContract.V {

        @Override
        public void setActualPaymentMoney() {
            mFlowRate = mEtFlowRate.getText().toString().trim();
            String num = String.format(getString(R.string.flow_rate_amount), TextUtil.isEmpty(mFlowRate) ? 0 : Integer.valueOf(mFlowRate) * 2);
            mTvMoney.setText(num);
        }

        @Override
        public void setInputRule() {
            Editable s = mEtFlowRate.getEditableText();

            int editStart = mEtFlowRate.getSelectionStart();
            int editEnd = mEtFlowRate.getSelectionEnd();

            Pattern pattern = Pattern.compile("0[0-9]");
            Matcher matcher = pattern.matcher(s.toString());
            // 如果是以0开头截断后面的字符串
            if (matcher.matches()) {
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            mEtFlowRate.setSelection(editStart);
        }

        @Override
        public void setPayStatus() {
            if (TextUtil.isNotEmpty(mFlowRate)) {
                mTvPay.setEnabled(true);
                ViewUtil.showView(mTvUnit);
            } else {
                mTvPay.setEnabled(false);
                ViewUtil.goneView(mTvUnit);
            }
        }

        @Override
        public void setHighlight(@IdRes int id) {
            View v = mChannelViews.getByKey(id);
            if (v == null) {
                return;
            }

            if (mPreChannelView == null) {
                mPreChannelView = v;
            } else {
                if (mPreChannelView.equals(v)) {
                    return;
                }
                mPreChannelView.setSelected(false);
                mPreChannelView = v;
            }
            v.setSelected(true);
        }

        @Override
        public void setSurplusFlowRate() {
            mTvSurplus.setText(Profile.inst().getInt(TProfile.flux) / KFlowConversion + KSurplusFlowUnit);
            mEtFlowRate.setText("");
        }

        @Override
        public void setPayPalPay(String orderId) {
            mReqCode = KPayPalPayCode;
            mOrderId = orderId;
            PayAction.payPalPay(FlowRateManageActivity.this, String.valueOf(mRechargeSum));
        }

        @Override
        public void setPingPay(String info) {
            mReqCode = KPingReqCode;
            PayAction.pingPay(FlowRateManageActivity.this, info);
        }

        @Override
        public void setResultDeal(int requestCode, int resultCode, Intent data) {
            final PayResult payResult = new PayResult();

            if (mReqCode == KPingReqCode) {
                payResult.put(TPayResult.type, PayType.pingPP);
            } else if (mReqCode == KPayPalPayCode) {
                payResult.put(TPayResult.type, PayType.payPal);
                if (data != null) {
                    data.putExtra(PayPalPay.KExtraOrderId, mOrderId);
                } else {
                    return;
                }
            }
            payResult.put(TPayResult.requestCode, requestCode);
            payResult.put(TPayResult.resultCode, resultCode);
            payResult.put(TPayResult.data, data);

            PayAction.onResult(payResult, new OnPayListener() {

                @Override
                public void onPaySuccess() {
                    Profile.inst().increase(TProfile.flux, mRechargeSum * KFlowConversion);
                    Profile.inst().saveToSp();

                    mView.setSurplusFlowRate();
                    showToast(R.string.flow_rate_pay_success);
                }

                @Override
                public void onPayError(String error) {
                    showToast(R.string.flow_rate_pay_fail);
                }
            });
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }
    }
}

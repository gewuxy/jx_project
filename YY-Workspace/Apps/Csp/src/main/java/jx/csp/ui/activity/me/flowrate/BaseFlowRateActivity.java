package jx.csp.ui.activity.me.flowrate;

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
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.model.MapList;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import pay.PayAction;

/**
 * @auther Huoxuyu
 * @since 2017/11/7
 */

abstract public class BaseFlowRateActivity extends BaseActivity {

    protected final String KSurplusFlowUnit = "G";
    protected final int KFlowConversion = 1024;

    protected final int KPingReqCode = 0;
    protected final int KPayPalPayCode = 1;

    protected MapList<Integer, View> mChannelViews;
    protected View mPreChannelView;

    protected String mFlowRate;
    protected int mRechargeSum;
    protected int mReqCode;//识别号，区分支付方式

    protected FlowRateContract.P mPresenter;
    protected FlowRateContract.V mView;

    protected EditText mEtFlowRate;

    protected TextView mTvSurplus;
    protected TextView mTvUnit;
    protected TextView mTvMoney;
    protected TextView mTvPay;

    @Override
    public void initData() {
        PayAction.startPayPalService(this);

        mChannelViews = new MapList<>();

        mView = new FlowRateViewImpl();
        mPresenter = new FlowRatePresenterImpl(mView);
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

    private void findChannelView(int id) {
        mChannelViews.add(Integer.valueOf(id), findView(id));
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.flow_rate_tv_pay);

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
                mView.setInput();
                mView.setPayMoney();
                mView.setPayStatus();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.setCallBack(mReqCode, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayAction.stopPayPalService(this);
    }

    @NonNull
    @Override
    abstract public int getContentViewId();

    private class FlowRateViewImpl implements FlowRateContract.V {

        @Override
        public void setPayMoney() {
            mFlowRate = mEtFlowRate.getText().toString().trim();
            String num = null;
            if (Util.checkAppCn()) {
                num = String.format(getString(R.string.flow_rate_amount), TextUtil.isEmpty(mFlowRate) ? 0 : Integer.valueOf(mFlowRate) * 2);
            } else {
                num = String.format(getString(R.string.flow_rate_amount), TextUtil.isEmpty(mFlowRate) ? 0 : Integer.valueOf(mFlowRate));
            }
            mTvMoney.setText(num);
        }

        @Override
        public void setInput() {
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
                showView(mTvUnit);
            } else {
                mTvPay.setEnabled(false);
                goneView(mTvUnit);
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
            float flux = Profile.inst().getFloat(TProfile.flux) / KFlowConversion;
            mTvSurplus.setText(String.format("%.2f", flux) + KSurplusFlowUnit);
            mEtFlowRate.setText("");
        }

        @Override
        public void setPayPalPay() {
            mReqCode = KPayPalPayCode;
            PayAction.payPalPay(BaseFlowRateActivity.this, String.valueOf(mRechargeSum));
        }

        @Override
        public void setPingPay(String info) {
            mReqCode = KPingReqCode;
            PayAction.pingPay(BaseFlowRateActivity.this, info);
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {

        }
    }
}

package jx.csp.ui.activity.me.flowrate;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.contact.FlowRateContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.presenter.FlowRatePresenterImpl;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
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

    protected View mPreChannelView;

    protected String mFlowRate;
    protected int mRechargeSum;
    protected int mReqCode;//识别号，区分支付方式

    protected FlowRateContract.P mPresenter;
    protected FlowRateContract.V mView;

    protected EditText mEtFlowRate;

    private View mViewList;
    protected TextView mTvSurplus;
    protected TextView mPriceRmb1;
    protected TextView mPriceRmb2;
    protected TextView mPriceRmb3;
    protected TextView mPriceRmb4;
    protected TextView mTvPay;

    @Override
    public void initData() {
        PayAction.startPayPalService(this);

        mView = new FlowRateViewImpl();
        mPresenter = new FlowRatePresenterImpl(mView);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getString(R.string.flow_rate_manage), this);
    }

    @Override
    public void findViews() {
        mTvSurplus = findView(R.id.flow_rate_tv_surplus);
        mTvPay = findView(R.id.flow_rate_tv_pay);

        mPriceRmb1 = findView(R.id.flow_rate_price_rmb_1);
        mPriceRmb2 = findView(R.id.flow_rate_price_rmb_2);
        mPriceRmb3 = findView(R.id.flow_rate_price_rmb_3);
        mPriceRmb4 = findView(R.id.flow_rate_price_rmb_4);


        sort(R.id.flow_rate_iv_alipay);
        sort(R.id.flow_rate_iv_wechat);
        sort(R.id.flow_rate_iv_unionpay);
        sort(R.id.flow_rate_iv_paypal);

        sort(R.id.flow_rate_layout_price_flow_1);
        sort(R.id.flow_rate_layout_price_flow_2);
        sort(R.id.flow_rate_layout_price_flow_3);
        sort(R.id.flow_rate_layout_price_flow_4);

        sort(R.id.flow_rate_layout_price_usd_five);
        sort(R.id.flow_rate_layout_price_usd_twenty_five);
        sort(R.id.flow_rate_layout_price_usd_one_hundred);
        sort(R.id.flow_rate_layout_price_usd_five_hundred);

        sort(R.id.flow_rate_cny_currency);
        sort(R.id.flow_rate_usd_currency);

    }

    protected View sort(@IdRes int id) {
        mViewList = findView(id);
        setOnClickListener(mViewList);
        return mViewList;
    }

    public void setPriceText(String id, TextView tv) {
        if (tv == null && TextUtil.isEmpty(id)) {
            return;
        }
        SpannableString text = new SpannableString(id);
        text.setSpan(new ForegroundColorSpan(ResLoader.getColor(R.color.text_167afe)), 2, id.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(text);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.flow_rate_tv_pay);

        mView.setSurplusFlowRate();
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
        public View setHighlight(View v, View view) {
            if (v == null) {
                return null;
            }
            if (view == null) {
                v.setSelected(true);
            } else {
                if (view.getId() != v.getId()) {
                    v.setSelected(true);
                    view.setSelected(false);
                }
            }
            return v;
        }

        @Override
        public void setSurplusFlowRate() {
            float flux = Profile.inst().getFloat(TProfile.flux) / KFlowConversion;
            mTvSurplus.setText(String.format("%.2f", flux) + KSurplusFlowUnit);
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

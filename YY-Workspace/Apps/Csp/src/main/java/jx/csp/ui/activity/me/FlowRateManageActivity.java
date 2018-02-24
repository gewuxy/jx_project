package jx.csp.ui.activity.me;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.adapter.FlowRateAdapter;
import jx.csp.adapter.PaymentAdapter;
import jx.csp.constant.PayType;
import jx.csp.constant.PriceValue;
import jx.csp.contact.FlowRateContract;
import jx.csp.model.FlowRate;
import jx.csp.model.FlowRate.TFlow;
import jx.csp.model.Payment;
import jx.csp.model.Payment.TPayment;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.presenter.FlowRatePresenterImpl;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ConstantsEx;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import pay.PayAction;
import pay.PingPay.PingPayChannel;

/**
 * 流量管理
 *
 * @auther HuoXuYu
 * @since 2017/11/7
 */

public class FlowRateManageActivity extends BaseActivity {

    private final String KSurplusFlowUnit = "G";
    private final int KFlowConversion = 1024;

    private final int KPingReqCode = 0;
    private final int KPayPalPayCode = 1;

    private int mReqCode;           //识别号，区分支付方式
    private int mFlow;              //流量值
    private int mPricePosition;     //售价标识
    private int mPaymentPosition;   //支付方式标识

    private View mViewCnyCurrency;  //支付货币:人民币
    private View mViewUsdCurrency;  //支付货币:美元
    private TextView mTvSurplus;

    private RecyclerView mRvPrice;
    private RecyclerView mRvPayment;

    private FlowRateAdapter mFlowRateAdapter;
    private PaymentAdapter mPaymentAdapter;

    private FlowRateContract.P mPresenter;
    private FlowRateContract.V mView;

    @Override
    public void initData() {
        PayAction.startPayPalService(this, BuildConfig.DEBUG_NETWORK);

        mPricePosition = ConstantsEx.KInvalidValue;
        mPaymentPosition = ConstantsEx.KInvalidValue;

        mView = new FlowRateViewImpl();
        mPresenter = new FlowRatePresenterImpl(mView);
    }

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
        mTvSurplus = findView(R.id.flow_rate_tv_surplus);
        mRvPrice = findView(R.id.flow_rate_rv_price);
        mRvPayment = findView(R.id.flow_rate_rv_payment);

        mViewCnyCurrency = findView(R.id.flow_rate_cny_currency);
        mViewUsdCurrency = findView(R.id.flow_rate_usd_currency);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.flow_rate_tv_pay);
        setOnClickListener(R.id.flow_rate_cny_currency);
        setOnClickListener(R.id.flow_rate_usd_currency);

        if (Util.checkAppCn()) {
            mViewCnyCurrency.setSelected(true);
        } else {
            mViewUsdCurrency.setSelected(true);
        }

        mView.setSurplusFlowRate();

        //售价
        mRvPrice.setLayoutManager(new GridLayoutManager(this, 2));
        mFlowRateAdapter = new FlowRateAdapter();
        mPricePosition = 2;
        mFlowRateAdapter.setData(getFlowData());
        mRvPrice.setAdapter(mFlowRateAdapter);
        mFlowRateAdapter.setOnAdapterClickListener(new FlowAdApterListener());

        //支付方式
        mRvPayment.setLayoutManager(new GridLayoutManager(this, 3));
        mPaymentAdapter = new PaymentAdapter();
        mPaymentAdapter.setData(getPaymentData());
        mRvPayment.setAdapter(mPaymentAdapter);
        mPaymentAdapter.setOnAdapterClickListener(new PayAdApterListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flow_rate_cny_currency:
            case R.id.flow_rate_usd_currency: {
                if (Util.checkAppCn()) {
                    if (v.getId() == mViewCnyCurrency.getId()) {
                        return;
                    }
                    mView.setHighlight(v, mViewCnyCurrency);
                    mViewCnyCurrency = v;
                } else {
                    if (v.getId() == mViewUsdCurrency.getId()) {
                        return;
                    }
                    mView.setHighlight(v, mViewUsdCurrency);
                    mViewUsdCurrency = v;
                }

                mFlowRateAdapter.setData(getFlowData());
                mFlowRateAdapter.notifyDataSetChanged();

                mPaymentAdapter.setData(getPaymentData());
                mPaymentAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.flow_rate_tv_pay: {
                int id = mPaymentAdapter.getItem(mPaymentPosition).getInt(TPayment.id);
                mFlow = mFlowRateAdapter.getItem(mPricePosition).getInt(TFlow.flow);

                // 1) 请求服务器获取charge
                refresh(RefreshWay.dialog);
                switch (id) {
                    case PayType.alipay: {
                        mPresenter.confirmPay(KPingReqCode, mFlow, PingPayChannel.alipay);
                    }
                    break;
                    case PayType.wechat: {
                        mPresenter.confirmPay(KPingReqCode, mFlow, PingPayChannel.wechat);
                    }
                    break;
                    case PayType.unionpay: {
                        mPresenter.confirmPay(KPingReqCode, mFlow, PingPayChannel.upacp);
                    }
                    break;
                    case PayType.paypal: {
                        mPresenter.confirmPay(KPayPalPayCode, mFlow, null);
                    }
                    break;
                }
            }
            break;
        }
    }

    private List<Payment> getPaymentData() {
        List<Payment> list = new ArrayList<>();
        if (Util.checkAppCn()) {
            if (mViewCnyCurrency.getId() == R.id.flow_rate_cny_currency) {
                list.add(new Payment(PayType.alipay, R.drawable.flow_rate_ic_alipay, true));
                list.add(new Payment(PayType.wechat, R.drawable.flow_rate_ic_wechat, false));
                list.add(new Payment(PayType.unionpay, R.drawable.flow_rate_ic_unionpay, false));
            } else {
                list.add(new Payment(PayType.paypal, R.drawable.flow_rate_ic_paypal, true));
            }
        } else {
            if (mViewUsdCurrency.getId() == R.id.flow_rate_usd_currency) {
                list.add(new Payment(PayType.paypal, R.drawable.flow_rate_ic_paypal, true));
            } else {
                list.add(new Payment(PayType.alipay, R.drawable.flow_rate_ic_alipay, true));
                list.add(new Payment(PayType.wechat, R.drawable.flow_rate_ic_wechat, false));
                list.add(new Payment(PayType.unionpay, R.drawable.flow_rate_ic_unionpay, false));
            }
        }
        mPaymentPosition = 0;
        return list;
    }

    /**
     * 流量选择
     * @return
     */
    private List<FlowRate> getFlowData() {
        if (Util.checkAppCn()) {
            if (mViewCnyCurrency.getId() == R.id.flow_rate_cny_currency) {
                return getCnyData();
            } else {
                return getUsdData();
            }
        } else {
            if (mViewUsdCurrency.getId() == R.id.flow_rate_usd_currency) {
                return getUsdData();
            } else {
                return getCnyData();
            }
        }
    }

    private List<FlowRate> getUsdData() {
        List<FlowRate> list = new ArrayList<>();
        list.add(new FlowRate(PriceValue.flow1, PriceValue.usdPrice1, getString(R.string.flow_rate_unit_en), false));
        list.add(new FlowRate(PriceValue.flow2, PriceValue.usdPrice2, getString(R.string.flow_rate_unit_en), false));
        list.add(new FlowRate(PriceValue.flow3, PriceValue.usdPrice3, getString(R.string.flow_rate_unit_en), false));
        list.add(new FlowRate(PriceValue.flow4, PriceValue.usdPrice4, getString(R.string.flow_rate_unit_en), false));

        if (list.size() > mPricePosition && mPricePosition >= 0) {
            FlowRate flowRate = list.get(mPricePosition);
            flowRate.put(TFlow.select, true);
        }
        return list;
    }

    private List<FlowRate> getCnyData() {
        List<FlowRate> list = new ArrayList<>();
        list.add(new FlowRate(PriceValue.flow1, PriceValue.cnyPrice1, getString(R.string.flow_rate_unit), false));
        list.add(new FlowRate(PriceValue.flow2, PriceValue.cnyPrice2, getString(R.string.flow_rate_unit), false));
        list.add(new FlowRate(PriceValue.flow3, PriceValue.cnyPrice3, getString(R.string.flow_rate_unit), false));
        list.add(new FlowRate(PriceValue.flow4, PriceValue.cnyPrice4, getString(R.string.flow_rate_unit), false));

        if (list.size() > mPricePosition && mPricePosition >= 0) {
            FlowRate flowRate = list.get(mPricePosition);
            flowRate.put(TFlow.select, true);
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.getCallBack(mReqCode, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayAction.stopPayPalService(this);
        mPresenter.onDestroy();
    }

    private class FlowRateViewImpl implements FlowRateContract.V {

        @Override
        public void setHighlight(View targetView, View view) {
            if (targetView == null) {
                return;
            }
            if (view == null) {
                targetView.setSelected(true);
            } else {
                if (view.getId() != targetView.getId()) {
                    targetView.setSelected(true);
                    view.setSelected(false);
                }
            }
        }

        @Override
        public void setSurplusFlowRate() {
            float flux = Profile.inst().getFloat(TProfile.flux) / KFlowConversion;
            mTvSurplus.setText(String.format("%.2f", flux) + KSurplusFlowUnit);
        }

        @Override
        public void setPayPalPay() {
            mReqCode = KPayPalPayCode;
            String money = null;
            switch (mFlow) {
                case 5: {
                    money = PriceValue.usdPrice1;
                }
                break;
                case 25: {
                    money = PriceValue.usdPrice2;
                }
                break;
                case 100: {
                    money = PriceValue.usdPrice3;
                }
                break;
                case 500: {
                    money = PriceValue.usdPrice4;
                }
                break;
            }
            PayAction.payPalPay(FlowRateManageActivity.this, money, BuildConfig.DEBUG_NETWORK);
        }

        @Override
        public void setPingPay(String info) {
            mReqCode = KPingReqCode;
            PayAction.pingPay(FlowRateManageActivity.this, info);
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {

        }
    }

    private class PayAdApterListener implements OnAdapterClickListener {

        @Override
        public void onAdapterClick(int position, View v) {
            if (mPaymentPosition == position) {
                return;
            }
            mPaymentAdapter.getItem(mPaymentPosition).put(TPayment.select, false);
            mPaymentAdapter.getItem(position).put(TPayment.select, true);
            mPaymentAdapter.notifyDataSetChanged();
            mPaymentPosition = position;
        }
    }

    private class FlowAdApterListener implements OnAdapterClickListener {

        @Override
        public void onAdapterClick(int position, View v) {
            if (mPricePosition == position) {
                return;
            }
            mFlowRateAdapter.getItem(mPricePosition).put(TFlow.select, false);
            mFlowRateAdapter.getItem(position).put(TFlow.select, true);
            mFlowRateAdapter.notifyDataSetChanged();
            mPricePosition = position;
        }
    }
}

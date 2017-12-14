package jx.csp.ui.activity.me;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jx.csp.R;
import jx.csp.adapter.FlowRateAdapter;
import jx.csp.adapter.PaymentAdapter;
import jx.csp.constant.FlowType;
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
 * @auther Huoxuyu
 * @since 2017/11/7
 */

public class FlowRateManageActivity extends BaseActivity {

    private final String KSurplusFlowUnit = "G";
    private final int KFlowConversion = 1024;

    private final int KPingReqCode = 0;
    private final int KPayPalPayCode = 1;

    private int mReqCode;//识别号，区分支付方式

    private FlowRateContract.P mPresenter;
    private FlowRateContract.V mView;

    private int mFlow;
    private int mPricePosition;
    private int mPaymentPosition;

    private View mViewCnyCurrency;
    private View mViewUsdCurrency;
    private TextView mTvSurplus;

    private RecyclerView mRvPrice;
    private RecyclerView mRvPayment;

    private FlowRateAdapter mFlowRateAdapter;
    private PaymentAdapter mPaymentAdapter;

    @Override
    public void initData() {
        PayAction.startPayPalService(this);

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
                    case FlowType.alipay: {
                        mPresenter.confirmPay(KPingReqCode, mFlow, PingPayChannel.alipay);
                    }
                    break;
                    case FlowType.wechat: {
                        mPresenter.confirmPay(KPingReqCode, mFlow, PingPayChannel.wechat);
                    }
                    break;
                    case FlowType.unionpay: {
                        mPresenter.confirmPay(KPingReqCode, mFlow, PingPayChannel.upacp);
                    }
                    break;
                    case FlowType.paypal: {
                        mPresenter.confirmPay(KPayPalPayCode, mFlow, null);
                    }
                    break;
                }
            }
            break;
        }
    }

    private List<Payment> getPaymentData() {
        int[] image;
        int[] id;
        if (mViewCnyCurrency.getId() == R.id.flow_rate_cny_currency) {
            image = new int[]{
                    R.drawable.flow_rate_ic_alipay,
                    R.drawable.flow_rate_ic_wechat,
                    R.drawable.flow_rate_ic_unionpay,
            };
            id = new int[]{
                    FlowType.alipay,
                    FlowType.wechat,
                    FlowType.unionpay
            };
        } else {
            image = new int[]{
                    R.drawable.flow_rate_ic_paypal,
            };
            id = new int[]{
                    FlowType.paypal
            };
        }

        List<Payment> list = new ArrayList<>();
        for (int i = 0; i < image.length; i++) {
            Payment payment = new Payment();
            payment.put(TPayment.image, image[i]);
            payment.put(TPayment.id, id[i]);
            if (i == 0) {
                payment.put(TPayment.select, true);
                mPaymentPosition = 0;
            }
            list.add(payment);
        }
        return list;
    }

    private List<FlowRate> getFlowData() {
        String[] flow;
        String[] currency;
        String[] price;
        flow = new String[]{
                PriceValue.flow1,
                PriceValue.flow2,
                PriceValue.flow3,
                PriceValue.flow4,
        };

        if (mViewCnyCurrency.getId() == R.id.flow_rate_cny_currency) {
            price = new String[]{
                    PriceValue.cnyPrice1,
                    PriceValue.cnyPrice2,
                    PriceValue.cnyPrice3,
                    PriceValue.cnyPrice4,
            };
            currency = new String[]{
                    getString(R.string.flow_rate_unit),
                    getString(R.string.flow_rate_unit),
                    getString(R.string.flow_rate_unit),
                    getString(R.string.flow_rate_unit),
            };
        } else {
            price = new String[]{
                    PriceValue.usdPrice1,
                    PriceValue.usdPrice2,
                    PriceValue.usdPrice3,
                    PriceValue.usdPrice4,
            };
            currency = new String[]{
                    getString(R.string.flow_rate_unit_en),
                    getString(R.string.flow_rate_unit_en),
                    getString(R.string.flow_rate_unit_en),
                    getString(R.string.flow_rate_unit_en),
            };
        }

        List<FlowRate> list = new ArrayList<>();
        int length = Math.min(Math.min(flow.length, price.length), currency.length);
        for (int i = 0; i < length; i++) {
            FlowRate flowRate = new FlowRate();
            flowRate.put(TFlow.flow, flow[i]);
            flowRate.put(TFlow.price, price[i]);
            flowRate.put(TFlow.currency, currency[i]);
            if (mPricePosition == ConstantsEx.KInvalidValue) {
                // 没选择过
                flowRate.put(TFlow.select, true);
                mPricePosition = 0;
            } else {
                if (mPricePosition == i) {
                    // 找到选择过的
                    flowRate.put(TFlow.select, true);
                }
            }
            list.add(flowRate);
        }
        return list;
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
            PayAction.payPalPay(FlowRateManageActivity.this, mFlow);
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
            if (mPaymentPosition != ConstantsEx.KInvalidValue) {
                mPaymentAdapter.getItem(mPaymentPosition).put(TPayment.select, false);
                mPaymentAdapter.invalidate(mPaymentPosition);
            }
            mPaymentAdapter.getItem(position).put(TPayment.select, true);
            mPaymentAdapter.invalidate(position);
            mPaymentPosition = position;
        }

    }

    private class FlowAdApterListener implements OnAdapterClickListener {

        @Override
        public void onAdapterClick(int position, View v) {
            if (mPricePosition == position) {
                return;
            }
            if (mPricePosition != ConstantsEx.KInvalidValue) {
                mFlowRateAdapter.getItem(mPricePosition).put(TFlow.select, false);
                mFlowRateAdapter.invalidate(mPricePosition);
            }
            mFlowRateAdapter.getItem(position).put(TFlow.select, true);
            mFlowRateAdapter.invalidate(position);
            mPricePosition = position;
        }

    }
}

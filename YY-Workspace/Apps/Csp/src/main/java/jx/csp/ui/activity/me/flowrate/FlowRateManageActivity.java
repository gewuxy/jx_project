package jx.csp.ui.activity.me.flowrate;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import jx.csp.R;
import lib.ys.config.AppConfig.RefreshWay;
import pay.PingPay.PingPayChannel;

/**
 * 流量管理
 *
 * @auther Huoxuyu
 * @since 2017/10/9
 */

public class FlowRateManageActivity extends BaseFlowRateActivity {

    protected View mViewPrice;
    protected View mViewCurrency;
    protected View mViewPayment;

    private ImageView mAlipay;
    private LinearLayout mPriceFlow1;
    private LinearLayout mCurrency;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_flow_rate_manage;
    }

    @Override
    public void findViews() {
        super.findViews();
        mAlipay = findView(R.id.flow_rate_iv_alipay);
        mPriceFlow1 = findView(R.id.flow_rate_layout_price_flow_1);
        mCurrency = findView(R.id.flow_rate_cny_currency);
    }

    @Override
    public void setViews() {
        super.setViews();

        mAlipay.setSelected(true);
        mPriceFlow1.setSelected(true);
        mCurrency.setSelected(true);

        String rmb1 = mPriceRmb1.getText().toString();
        String rmb2 = mPriceRmb2.getText().toString();
        String rmb3 = mPriceRmb3.getText().toString();
        String rmb4 = mPriceRmb4.getText().toString();

        setPriceText(rmb1, mPriceRmb1);
        setPriceText(rmb2, mPriceRmb2);
        setPriceText(rmb3, mPriceRmb3);
        setPriceText(rmb4, mPriceRmb4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // FIXME: 2017/11/22 1.0版本暂时没有银联
            case R.id.flow_rate_iv_unionpay:
            case R.id.flow_rate_iv_wechat:
            case R.id.flow_rate_iv_alipay: {
                mView.setHighlight(v , mViewPayment);
                mViewPayment = v;
            }
            break;
            case R.id.flow_rate_layout_price_flow_1:
            case R.id.flow_rate_layout_price_flow_2:
            case R.id.flow_rate_layout_price_flow_3:
            case R.id.flow_rate_layout_price_flow_4:{
                mView.setHighlight(v , mViewPrice);
                mViewPrice = v;
            }
            break;
            case R.id.flow_rate_usd_currency:
            case R.id.flow_rate_cny_currency: {
                mView.setHighlight(v , mViewCurrency);
                mViewCurrency = v;
            }
            break;
            case R.id.flow_rate_tv_pay: {
                // 1) 请求服务器获取charge
                refresh(RefreshWay.dialog);
                switch (mViewPayment.getId()) {
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
                }
            }
            break;
        }
    }

}

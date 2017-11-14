package jx.csp.ui.activity.me;

import android.support.annotation.NonNull;
import android.view.View;

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

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_flow_rate_manage;
    }

    @Override
    public void setViews() {
        super.setViews();
        for (View v : mChannelViews) {
            setOnClickListener(v);
        }

        // 初始化高亮第一个
        mView.setHighlight(mChannelViews.get(0).getId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flow_rate_iv_unionpay:
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
                }
            }
            break;
        }
    }

}

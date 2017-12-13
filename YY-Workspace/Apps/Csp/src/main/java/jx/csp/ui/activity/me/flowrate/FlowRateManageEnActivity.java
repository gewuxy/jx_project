package jx.csp.ui.activity.me.flowrate;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import jx.csp.R;
import lib.ys.config.AppConfig.RefreshWay;

/**
 * 流量管理
 *
 * @auther Huoxuyu
 * @since 2017/11/7
 */

public class FlowRateManageEnActivity extends FlowRateManageActivity {

    private ImageView mIvPayPal;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_flow_rate_manage_en;
    }

    @Override
    public void findViews() {
        super.findViews();

        mIvPayPal = findView(R.id.flow_rate_iv_paypal);
    }

    @Override
    public void setViews() {
        super.setViews();
        mIvPayPal.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flow_rate_tv_pay: {
                mRechargeSum = Integer.valueOf(mFlowRate);
                if (mRechargeSum == 0) {
                    showToast(R.string.flow_rate_input_top_up);
                    return;
                }
                // 1) 请求服务器获取charge
                refresh(RefreshWay.dialog);
                mPresenter.confirmPay(KPayPalPayCode, mRechargeSum, null);
            }
            break;
        }
    }


}

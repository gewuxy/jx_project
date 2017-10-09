package jx.csp.ui.activity.me;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * @auther Huoxuyu
 * @since 2017/10/9
 */

public class FlowRateManageActivity extends BaseActivity{

    private EditText mEtRecharge;

    private TextView mTvSurplus;
    private TextView mTvUnit;
    private TextView mTvMoney;
    private TextView mTvPay;

    private ImageView mIvAlipay;
    private ImageView mIvWechat;
    private ImageView mIvUnionpay;
    private ImageView mIvPaypal;

    private int mRechargeSum;

    @Override
    public void initData() {

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

        mIvAlipay = findView(R.id.flow_rate_iv_alipay);
        mIvWechat = findView(R.id.flow_rate_iv_wechat);
        mIvUnionpay = findView(R.id.flow_rate_iv_unionpay);
        mIvPaypal = findView(R.id.flow_rate_iv_paypal);
    }

    @Override
    public void setViews() {
        mTvPay.setEnabled(false);
        setOnClickListener(R.id.flow_rate_tv_pay);
        mTvSurplus.setText(Profile.inst().getString(TProfile.flowrate));

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
                String str = mEtRecharge.getText().toString().trim();
                String num = String.format(getString(R.string.flow_rate_amount), TextUtil.isEmpty(str) ? 0 : Integer.valueOf(str) * 2);

                mTvMoney.setText(num);
                mEtRecharge.addTextChangedListener(this);

            }
        });
    }

    @Override
    public void onClick(View v) {
        String Num = mEtRecharge.getText().toString().trim();
        if (mRechargeSum == 0) {
            showToast(R.string.flow_rate_input_top_up);
            return;
        }
        mRechargeSum = Integer.valueOf(Num);

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return super.onNetworkResponse(id, r);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

    }
}

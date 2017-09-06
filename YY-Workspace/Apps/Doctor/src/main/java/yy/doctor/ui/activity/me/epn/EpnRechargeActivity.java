package yy.doctor.ui.activity.me.epn;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alipay.Pay;
import alipay.PayResult;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.me.EpnRecharge;
import yy.doctor.model.me.EpnRecharge.TEpnRecharge;
import yy.doctor.network.NetworkAPISetter;
import yy.doctor.util.Util;


/**
 * 象数充值
 *
 * @author CaiXiang
 * @since 2017/4/14
 */
public class EpnRechargeActivity extends BaseActivity {

    private TextView mTvEpn;
    private TextView mTvEpnRechargeNum;
    private EditText mEtRechargeNum;
    private TextView mTvRecharge;

    private int mRechargeSum;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                PayResult payResult = new PayResult((String) msg.obj);
                /**
                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                 * docType=1) 建议商户依赖异步通知
                 */
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    showToast(R.string.pay_success);
                    //支付成功后显示充值后的的象数数量  改变本地的象数数量
                    int epnNum = Profile.inst().getInt(TProfile.credits) + mRechargeSum * 10;
                    Profile.inst().put(TProfile.credits, epnNum);
                    Profile.inst().saveToSp();
                    mTvEpn.setText(epnNum + "");
                    EpnRechargeActivity.this.notify(NotifyType.profile_change);
                } else {
                    // 判断resultStatus 为非"9000"则代表可能支付失败
                    // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        showToast(R.string.pay_ing);
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        showToast(R.string.pay_fail);
                    }
                }
            }
        }
    };

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_epn_recharge;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, R.string.epn_recharge, this);
    }

    @Override
    public void findViews() {

        mTvEpn = findView(R.id.recharge_tv_remain_epn);
        mTvEpnRechargeNum = findView(R.id.recharge_tv_total);
        mEtRechargeNum = findView(R.id.recharge_et_num);
        mTvRecharge = findView(R.id.recharge_tv_pay);
    }

    @Override
    public void setViews() {

        mTvRecharge.setEnabled(false);
        setOnClickListener(R.id.recharge_tv_pay);
        mTvEpn.setText(Profile.inst().getString(TProfile.credits));

        mEtRechargeNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                int editStart = mEtRechargeNum.getSelectionStart();
                int editEnd = mEtRechargeNum.getSelectionEnd();
                // 先去掉监听器，否则会出现栈溢出
                mEtRechargeNum.removeTextChangedListener(this);
                Pattern pattern = Pattern.compile("0[0-9]");
                Matcher matcher = pattern.matcher(s.toString());
                // 如果是以0开头截断后面的字符串
                if (matcher.matches()) {
                    s.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
                mEtRechargeNum.setSelection(editStart);
                String editStr = mEtRechargeNum.getText().toString().trim();
                String epnNum = String.format(getString(R.string.epn_unit), TextUtil.isEmpty(editStr) ? 0 : Integer.valueOf(editStr) * 10);

                mTvEpnRechargeNum.setText(epnNum);
                // 恢复监听
                mEtRechargeNum.addTextChangedListener(this);

                if (TextUtil.isNotEmpty(editStr)) {
                    mTvRecharge.setEnabled(true);
                } else {
                    mTvRecharge.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.recharge_tv_pay: {
                String str = mEtRechargeNum.getText().toString().trim();
                if (mRechargeSum == 0) {
                    showToast(R.string.input_recharge_money);
                    return;
                }
                mRechargeSum = Integer.valueOf(str);

                exeNetworkReq(NetworkAPISetter.CommonAPI.epnRecharge(getString(R.string.epn_recharge), mRechargeSum).build());
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        EpnRecharge epnRecharge = new EpnRecharge();
        JSONObject object = new JSONObject(r.getText());
        epnRecharge.put(TEpnRecharge.data, object.getString("data"));
        epnRecharge.put(TEpnRecharge.code, object.getInt("code"));
        return epnRecharge;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        EpnRecharge r = (EpnRecharge) result;
        if (r.getInt(TEpnRecharge.code) == 0) {
            String info = r.getString(TEpnRecharge.data);
            YSLog.d(TAG, "OrderInfo = " + info);
            Pay.aliPay(EpnRechargeActivity.this, info, mHandler);
        } else {
            showToast(R.string.epn_recharge_fail);
        }
    }

}

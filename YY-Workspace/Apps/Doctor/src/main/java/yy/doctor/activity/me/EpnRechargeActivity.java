package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Resp;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.me.EpnRecharge;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
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

        Util.addBackIcon(bar, "象数充值", this);

    }

    @Override
    public void findViews() {

        mTvEpn = findView(R.id.recharge_tv_remain_epn);
        mTvEpnRechargeNum = findView(R.id.recharge_tv_total);
        mEtRechargeNum = findView(R.id.recharge_et_num);

    }

    @Override
    public void setViews() {

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
                long payayNum;
                String editStr = mEtRechargeNum.getText().toString().trim();
                if (!"".equals(editStr)) {
                    payayNum = Long.valueOf(editStr);
                    mTvEpnRechargeNum.setText(payayNum * 10 + "个象数");
                } else {
                    mTvEpnRechargeNum.setText("0个象数");
                }
                // 恢复监听
                mEtRechargeNum.addTextChangedListener(this);
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.recharge_tv_pay: {

                exeNetworkReq(0, NetFactory.epnRecharge("象数充值", "20"));
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), EpnRecharge.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        Resp<EpnRecharge> r = (Resp<EpnRecharge>) result;

        if (r.isSucceed()) {
            showToast("象数充值成功");
        } else {
            showToast(r.getError());
        }

    }

}

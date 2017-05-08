package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lib.network.model.NetworkResponse;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Response;
import yy.doctor.R;
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

    private TextView mTvPay;
    private TextView mTvEpnNum;
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

    }

    @Override
    public void setViews() {

        setOnClickListener(R.id.recharge_epn_tv_pay);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.recharge_epn_tv_pay: {

                exeNetworkRequest(0, NetFactory.epnRecharge("象数充值", "20"));
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        return JsonParser.ev(nr.getText(), EpnRecharge.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        Response<EpnRecharge> r = (Response<EpnRecharge>) result;

        if (r.isSucceed()) {
            showToast("象数充值成功");
        } else {
            showToast(r.getError());
        }

    }
}

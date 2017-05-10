package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.network.model.NetworkResponse;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Response;
import yy.doctor.R;
import yy.doctor.model.me.EpcDetail;
import yy.doctor.model.me.EpcDetail.TEpcDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 商品详情
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcDetailActivity extends BaseActivity {

    private NetworkImageView mIv;
    private TextView mTvName;
    private TextView mTvEpn;
    private TextView mTvLimitationNum;
    private TextView mTvDescription;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_epc_detail;
    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "xxx商品", this);

    }

    @Override
    public void findViews() {

        mIv = findView(R.id.epc_detail_iv);
        mTvName = findView(R.id.epc_detail_tv_name);
        mTvEpn = findView(R.id.epc_detail_tv_epn);
        mTvLimitationNum = findView(R.id.epc_detail_tv_limitation_num);
        mTvDescription = findView(R.id.epc_detail_tv_description);

    }

    @Override
    public void setViews() {

        refresh(RefreshWay.dialog);
        exeNetworkRequest(0 , NetFactory.epcDetail(00001));

        setOnClickListener(R.id.epc_detail_tv_btn);

        mIv.placeHolder(R.drawable.app_bg)
                .load();

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        return JsonParser.ev(nr.getText(), EpcDetail.class);
    }


    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        stopRefresh();
        Response<EpcDetail> r = (Response<EpcDetail>) result;
        if (r.isSucceed()) {
            EpcDetail epcDetail = r.getData();
            mTvName.setText(epcDetail.getString(TEpcDetail.name));
            mTvEpn.setText(epcDetail.getString(TEpcDetail.price));
            mTvDescription.setText(epcDetail.getString(TEpcDetail.description));

        }
        
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.epc_detail_tv_btn: {
                startActivity(ExchangeActivity.class);
            }
            break;
        }

    }

}

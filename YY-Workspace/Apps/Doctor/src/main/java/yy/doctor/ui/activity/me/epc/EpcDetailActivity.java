package yy.doctor.ui.activity.me.epc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.model.me.EpcDetail;
import yy.doctor.model.me.EpcDetail.TEpcDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.EpcAPI;
import yy.doctor.util.Util;

/**
 * 商品详情
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
@Route
public class EpcDetailActivity extends BaseActivity {

    @Arg(opt = true)
    int mGoodId;
    @Arg(opt = true)
    String mGoodName;
    @Arg(opt = true)
    String mSmallImgUrl;

    private NetworkImageView mIv;
    private TextView mTvName;
    private TextView mTvEpn;
    private TextView mTvDescription;

    private int mEpn;

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_epc_detail;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mGoodName, this);
    }

    @Override
    public void findViews() {

        mIv = findView(R.id.epc_detail_iv);
        mTvName = findView(R.id.epc_detail_tv_name);
        mTvEpn = findView(R.id.epc_detail_tv_epn);
        mTvDescription = findView(R.id.epc_detail_tv_description);
    }

    @Override
    public void setViews() {

        refresh(RefreshWay.dialog);
        mIv.placeHolder(R.drawable.app_bg)
                .load();
        setOnClickListener(R.id.epc_detail_tv_btn);

        exeNetworkReq(EpcAPI.epcDetail(mGoodId).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), EpcDetail.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            EpcDetail epcDetail = (EpcDetail) r.getData();
            mTvName.setText(epcDetail.getString(TEpcDetail.name));
            mEpn = epcDetail.getInt(TEpcDetail.price);
            mTvEpn.setText(epcDetail.getString(TEpcDetail.price));
            mTvDescription.setText(epcDetail.getString(TEpcDetail.description));
            mIv.placeHolder(R.drawable.app_bg)
                    .url(epcDetail.getString(TEpcDetail.picture))
                    .load();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.epc_detail_tv_btn: {
                ExchangeActivityRouter.create()
                        .goodId(mGoodId)
                        .goodName(mGoodName)
                        .epn(mEpn)
                        .url(mSmallImgUrl)
                        .route(this);
            }
            break;
        }
    }

}

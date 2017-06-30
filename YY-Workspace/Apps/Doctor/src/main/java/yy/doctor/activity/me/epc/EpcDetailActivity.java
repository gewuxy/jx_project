package yy.doctor.activity.me.epc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.me.EpcDetail;
import yy.doctor.model.me.EpcDetail.TEpcDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

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
    private TextView mTvDescription;

    private int mGoodId;
    private String mGoodName;
    private int mEpn;
    private String mSmallImgUrl;

    public static void nav(Context context, int goodId, String goodName, String url) {
        Intent i = new Intent(context, EpcDetailActivity.class);
        i.putExtra(Extra.KData, goodId);
        i.putExtra(Extra.KName, goodName);
        i.putExtra(Extra.KUrl, url);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        Intent i = getIntent();
        mGoodId = i.getIntExtra(Extra.KData, 000);
        mGoodName = i.getStringExtra(Extra.KName);
        mSmallImgUrl = i.getStringExtra(Extra.KUrl);
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

        exeNetworkReq(NetFactory.epcDetail(mGoodId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), EpcDetail.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        stopRefresh();
        Result<EpcDetail> r = (Result<EpcDetail>) result;
        if (r.isSucceed()) {
            EpcDetail epcDetail = r.getData();
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
                ExchangeActivity.nav(EpcDetailActivity.this, mGoodId, mGoodName, mEpn, mSmallImgUrl);
            }
            break;
        }
    }

}

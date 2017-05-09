package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;

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

        setOnClickListener(R.id.epc_detail_tv_btn);

        mIv.placeHolder(R.drawable.app_bg)
                .load();

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

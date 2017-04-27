package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;

import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
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

        mIv=findView(R.id.epc_detail_iv);

    }

    @Override
    public void setViews() {

        setOnClickListener(R.id.epc_detail_tv_btn);

        mIv.placeHolder(R.mipmap.ic_default_meeting_content_detail)
                .load();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id=v.getId();
        switch (id){
            case R.id.epc_detail_tv_btn: {
                startActivity(ExchangeActivity.class);
            }
            break;
        }

    }
}

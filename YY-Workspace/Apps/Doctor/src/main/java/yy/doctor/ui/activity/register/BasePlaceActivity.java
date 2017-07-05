package yy.doctor.ui.activity.register;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.ProvincesAdapter;
import yy.doctor.model.Provinces;
import yy.doctor.util.Util;

/**
 * @auther Huoxuyu
 * @since 2017/7/4
 */

public class BasePlaceActivity extends BaseSRListActivity<Provinces, ProvincesAdapter>{

    private View mLocationLayout;
    private ImageView mIvLocation;
    private TextView mTvLocation;
    private TextView mTvLocationFailure;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.province_city,this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mLocationLayout = findView(R.id.layout_province_location_load_layout);
        mIvLocation = findView(R.id.layout_province_location_load_iv);
        mTvLocation = findView(R.id.layout_province_city_area_header_tv);
        mTvLocationFailure = findView(R.id.layout_province_city_area_header_tv_failure);
    }

    @Override
    public void getDataFromNet() {

    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_province_city_area_header);
    }

    @Override
    public void setViews() {
        super.setViews();

        setAutoLoadMoreEnabled(false);
        if (ProvinceActivity.mLocation == null) {
            showView(mTvLocationFailure);
        } else {
            goneView(mTvLocationFailure);
            mTvLocation.setText(ProvinceActivity.mLocation);
        }
    }
}

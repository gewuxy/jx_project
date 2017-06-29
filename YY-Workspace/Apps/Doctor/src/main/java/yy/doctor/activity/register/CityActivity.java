package yy.doctor.activity.register;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.CityAdapter;
import yy.doctor.model.City;
import yy.doctor.model.City.TCity;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class CityActivity extends BaseSRListActivity<City, CityAdapter> {

    private static final int KCityRequestCode = 10;
    private String mProvinceId;
    private TextView mTvLocation;
    private TextView mTvLocationFailure;
    private String mProvince;

    public static void nav(Activity activity, String id, String province) {
        Intent i = new Intent(activity, CityActivity.class);
        i.putExtra(Extra.KData, id);
        i.putExtra(Extra.KProvince, province);
        LaunchUtil.startActivityForResult(activity, i, KCityRequestCode);
    }

    @Override
    public void initData() {
        mProvinceId = getIntent().getStringExtra(Extra.KData);
        mProvince = getIntent().getStringExtra(Extra.KProvince);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.province_city, this);
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_province_city_area_header);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvLocation = findView(R.id.layout_province_city_area_header_tv);
        mTvLocationFailure = findView(R.id.layout_province_city_area_header_tv_failure);
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

        setOnAdapterClickListener((position, v) -> {
            City city = getItem(position);
            //如果level等于3就没有下一级了，直接返回
            if (city.getInt(TCity.level) == 3) {
                Intent i = new Intent();
                i.putExtra(Extra.KProvince, mProvince);
                i.putExtra(Extra.KCity, city.getString(TCity.name));
                setResult(RESULT_OK, i);
                finish();
            } else {
                AreaActivity.nav(this, city.getString(TCity.id), city.getString(TCity.name));
            }
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.city(mProvinceId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String area = data.getStringExtra(Extra.KArea);
            String city = data.getStringExtra(Extra.KCity);
            Intent i = new Intent();
            i.putExtra(Extra.KProvince, mProvince);
            i.putExtra(Extra.KCity, city);
            i.putExtra(Extra.KArea, area);
            setResult(RESULT_OK, i);
            finish();
        }
    }

}

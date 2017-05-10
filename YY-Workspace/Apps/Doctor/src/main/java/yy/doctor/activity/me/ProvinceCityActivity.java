package yy.doctor.activity.me;

import android.content.Intent;
import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.CityFrag;
import yy.doctor.frag.CityFrag.OnCityListener;
import yy.doctor.frag.ProvinceFrag;
import yy.doctor.frag.ProvinceFrag.OnProvinceListener;
import yy.doctor.util.Util;

/**
 * 省市选择
 *
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ProvinceCityActivity extends BaseActivity implements OnProvinceListener, OnCityListener {

    private ProvinceFrag mFragProvince;
    private CityFrag mFragCity;

    private String mProvinceName;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_province_city;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "省市", this);
    }

    @Override
    public void findViews() {
        mFragProvince = findFragment(R.id.province_city_frag_province);
        mFragCity = findFragment(R.id.province_city_frag_city);
    }

    @Override
    public void setViews() {
        mFragProvince.setListener(this);
        mFragCity.setListener(this);
    }

    @Override
    public void onProvinceSelected(int position, String id, String name) {
        mFragCity.setProvinceId(id);
        mProvinceName = name;
    }

    @Override
    public void onCitySelected(int position, String name) {
        Intent intent = new Intent()
                .putExtra(Extra.KProvince, mProvinceName)
                .putExtra(Extra.KCity, name);
        setResult(RESULT_OK, intent);

        finish();
    }
}

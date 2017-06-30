package yy.doctor.ui.activity.register;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.AreaAdapter;
import yy.doctor.model.Area;
import yy.doctor.model.Area.TArea;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class AreaActivity extends BaseSRListActivity<Area, AreaAdapter> {

    private static final int KAreaRequestCode = 20;
    private String mCityId;
    private String mCity;
    private TextView mTvLocation;
    private TextView mTvLocationFailure;

    public static void nav(Activity activity, String id, String city) {
        Intent i = new Intent(activity, AreaActivity.class);
        i.putExtra(Extra.KData, id);
        i.putExtra(Extra.KCity, city);
        LaunchUtil.startActivityForResult(activity, i, KAreaRequestCode);
    }

    @Override
    public void initData() {
        mCityId = getIntent().getStringExtra(Extra.KData);
        mCity = getIntent().getStringExtra(Extra.KCity);
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
            Area area = getItem(position);
            Intent i = new Intent();
            i.putExtra(Extra.KArea, area.getString(TArea.name));
            i.putExtra(Extra.KCity, mCity);
            setResult(RESULT_OK, i);
            finish();
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.city(mCityId));
    }

}

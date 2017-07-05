package yy.doctor.ui.activity.register;

import android.app.Activity;
import android.content.Intent;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.model.Provinces;
import yy.doctor.model.Provinces.TProvinces;
import yy.doctor.network.NetFactory;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class CityActivity extends BasePlaceActivity {

    private static final int KCityRequestCode = 10;
    private String mProvinceId;
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
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener((position, v) -> {
            Provinces city = getItem(position);
            //如果level等于3就没有下一级了，直接返回
            if (city.getInt(TProvinces.level) == 3) {
                Place place = new Place();
                place.put(TPlace.province, mProvince);
                place.put(TPlace.city, city.getString(TProvinces.name));
                notify(NotifyType.province_finish, place);
                finish();
            } else {
                AreaActivity.nav(CityActivity.this, city.getString(TProvinces.id), mProvince, city.getString(TProvinces.name));
            }
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.city(mProvinceId));
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.province_finish) {
            finish();
        }

    }
}

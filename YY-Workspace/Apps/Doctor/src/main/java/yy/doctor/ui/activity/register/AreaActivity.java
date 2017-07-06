package yy.doctor.ui.activity.register;

import android.app.Activity;
import android.content.Intent;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.model.Pca;
import yy.doctor.model.Pca.TPca;
import yy.doctor.network.NetFactory;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class AreaActivity extends BasePcaActivity {

    private static final int KAreaRequestCode = 20;
    private String mCityId;
    private String mProvince;
    private String mCity;

    public static void nav(Activity activity, String id, String province, String city) {
        Intent i = new Intent(activity, AreaActivity.class);
        i.putExtra(Extra.KData, id);
        i.putExtra(Extra.KProvince, province);
        i.putExtra(Extra.KCity, city);
        LaunchUtil.startActivityForResult(activity, i, KAreaRequestCode);
    }

    @Override
    public void initData() {
        mCityId = getIntent().getStringExtra(Extra.KData);
        mProvince = getIntent().getStringExtra(Extra.KProvince);
        mCity = getIntent().getStringExtra(Extra.KCity);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener((position, v) -> {

            Pca area = getItem(position);
            Place place = new Place();
            place.put(TPlace.province, mProvince);
            place.put(TPlace.city, mCity);
            place.put(TPlace.district, area.getString(TPca.name));
            notify(NotifyType.province_finish, place);
            finish();
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.city(mCityId));
    }

}

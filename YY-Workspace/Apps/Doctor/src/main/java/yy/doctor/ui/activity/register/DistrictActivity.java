package yy.doctor.ui.activity.register;

import android.content.Context;
import android.content.Intent;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Pcd;
import yy.doctor.model.Pcd.TPcd;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class DistrictActivity extends BasePcdActivity {

    private final int KCommit = 1;

    private String mCityId;
    private String mProvince;
    private String mCity;

    /**
     * @param context
     * @param id       城市id
     * @param province
     * @param city
     */
    public static void nav(Context context, String id, String province, String city, String pcdDesc) {
        Intent i = new Intent(context, DistrictActivity.class)
                .putExtra(Extra.KPcdDesc, pcdDesc)
                .putExtra(Extra.KCityId, id)
                .putExtra(Extra.KCity, city)
                .putExtra(Extra.KProvince, province);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mCityId = getIntent().getStringExtra(Extra.KCityId);
        mCity = getIntent().getStringExtra(Extra.KCity);
        mProvince = getIntent().getStringExtra(Extra.KProvince);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.district, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        setLocation(getIntent().getStringExtra(Extra.KPcdDesc));
        setOnAdapterClickListener((position, v) -> {

            Pcd item = getItem(position);
            Place place = new Place();
            place.put(TPlace.province, mProvince);
            place.put(TPlace.city, mCity);
            place.put(TPlace.district, item.getString(TPcd.name));

            Profile.inst().put(TProfile.province, place.getString(TPlace.province));
            Profile.inst().put(TProfile.city, place.getString(TPlace.city));
            Profile.inst().put(TProfile.zone, place.getString(TPlace.district));
            Profile.inst().saveToSp();

            refresh(RefreshWay.embed);
            exeNetworkReq(NetFactory.newModifyBuilder()
                    .province(mProvince)
                    .city(mCity)
                    .area(item.getString(TPcd.name))
                    .build());
            notify(NotifyType.province_finish, place);
            finish();
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.city(mCityId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
    }
}

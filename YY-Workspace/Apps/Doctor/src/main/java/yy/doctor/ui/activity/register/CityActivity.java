package yy.doctor.ui.activity.register;

import android.content.Context;
import android.content.Intent;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.ys.ConstantsEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Pcd;
import yy.doctor.model.Pcd.TPcd;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class CityActivity extends BasePcdActivity {

    private String mPcdDesc;
    private String mProvinceId;
    private String mProvince;

    /**
     * @param context
     * @param pId      省份id
     * @param province
     * @param pcdDesc
     */
    public static void nav(Context context, String pId, String province, String pcdDesc) {
        Intent i = new Intent(context, CityActivity.class)
                .putExtra(Extra.KPcdDesc, pcdDesc)
                .putExtra(Extra.KProvinceId, pId)
                .putExtra(Extra.KProvince, province);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mProvinceId = getIntent().getStringExtra(Extra.KProvinceId);
        mProvince = getIntent().getStringExtra(Extra.KProvince);
        mPcdDesc = getIntent().getStringExtra(Extra.KPcdDesc);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.city, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        setLocation(mPcdDesc);

        setOnAdapterClickListener((position, v) -> {
            Pcd item = getItem(position);
            //如果level等于3就没有下一级了，直接返回
            if (item.getInt(TPcd.level) == Pcd.KLevelEnd) {
                Place place = new Place();
                place.put(TPlace.province, mProvince);
                place.put(TPlace.city, item.getString(TPcd.name));

                Profile.inst().put(TProfile.province, place.getString(TPlace.province));
                Profile.inst().put(TProfile.city, place.getString(TPlace.city));
                Profile.inst().put(TProfile.zone, ConstantsEx.KEmptyValue);
                Profile.inst().saveToSp();

                notify(NotifyType.province_finish, place);
                finish();
            } else {
                DistrictActivity.nav(CityActivity.this, item.getString(TPcd.id), mProvince, item.getString(TPcd.name), getLocation());
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

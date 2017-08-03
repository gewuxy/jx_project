package yy.doctor.ui.activity.register;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkResp;
import lib.network.model.err.ParseError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.model.Pcd;
import yy.doctor.model.Pcd.TPcd;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * @auther Huoxuyu
 * @since 2017/8/1
 */

abstract public class BasePcdLevel2Activity extends BasePcdActivity {

    private final int KIdCommit = 1;

    private int mCurrItem;

    private Place mPlace;

    @Override
    public void initData() {
        mProvinceId = getIntent().getStringExtra(Extra.KProvinceId);
        mCityId = getIntent().getStringExtra(Extra.KCityId);

        mProvince = getIntent().getStringExtra(Extra.KProvince);
        mCity = getIntent().getStringExtra(Extra.KCity);

        mPcdDesc = getIntent().getStringExtra(Extra.KPcdDesc);
    }

    @Override
    public void setViews() {
        super.setViews();

        setLocation(mPcdDesc);
        setOnAdapterClickListener((position, v) -> {
            mCurrItem = position;

            Pcd item = getItem(position);
            if (item.getInt(TPcd.level) != Pcd.KLevelEnd) {
                DistrictActivity.nav(this, item.getString(TPcd.id), mProvince, item.getString(TPcd.name), getLocation());
            } else {
                Place place = makePlace(item.getString(TPcd.name));

                refresh(RefreshWay.dialog);
                exeNetworkReq(KIdCommit, NetFactory.newModifyBuilder()
                        .province(place.getString(TPlace.province))
                        .city(place.getString(TPlace.city))
                        .area(place.getString(TPlace.district))
                        .build());
            }
        });
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KIdCommit) {
            return JsonParser.error(r.getText());
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdCommit) {
            Result r = (Result) result;
            if (r.isSucceed()) {
                Place place = makePlace(getItem(mCurrItem).getString(TPcd.name));

                Profile.inst().put(TProfile.province, place.getString(TPlace.province));
                Profile.inst().put(TProfile.city, place.getString(TPlace.city));
                Profile.inst().put(TProfile.zone, place.getString(TPlace.district));
                Profile.inst().saveToSp();

                stopRefresh();

                notify(NotifyType.province_finish, place);
                finish();
            } else {
                onNetworkError(id, new ParseError(r.getError()));
            }
        } else {
            super.onNetworkSuccess(id, result);
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.province_finish) {
            finish();
        }
    }

    private Place makePlace(String name) {
        //判断是否有赋值
        if (mPlace != null) {
            return mPlace;
        }

        //判断市区哪个为空
        String d = null;
        if (TextUtil.isEmpty(mCity)) {
            mCity = name;
        } else {
            d = name;
        }

        mPlace = new Place();
        mPlace.put(TPlace.province, mProvince);
        mPlace.put(TPlace.city, mCity);
        mPlace.put(TPlace.district, d);

        return mPlace;
    }
}

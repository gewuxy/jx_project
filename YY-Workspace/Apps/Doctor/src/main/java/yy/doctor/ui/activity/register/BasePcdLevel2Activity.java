package yy.doctor.ui.activity.register;

import android.content.Intent;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkResp;
import lib.processor.annotation.Extra;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.model.Pcd;
import yy.doctor.model.Pcd.TPcd;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * @auther Huoxuyu
 * @since 2017/8/1
 */

abstract public class BasePcdLevel2Activity extends BasePcdActivity {

    private final int KIdCommit = 1;

    @Extra(optional = true)
    String mPcdDesc;

    @Extra(optional = true)
    String mProvinceId;

    @Extra(optional = true)
    String mCityId;

    @Extra(optional = true)
    String mProvince;

    @Extra(optional = true)
    String mCity;

    @Extra(optional = true)
    String mDistrict;

    private int mCurrItem;

    @Override
    public void initData() {
    }

    @Override
    public void setViews() {
        super.setViews();

        setLocation(mPcdDesc);
        setOnAdapterClickListener((position, v) -> {
            mCurrItem = position;

            Pcd item = getItem(position);
            if (item.getInt(TPcd.level) != Pcd.KLevelEnd) {
//                DistrictActivity.nav(this, item.getString(TPcd.id), mProvince, item.getString(TPcd.name), getLocation());
                DistrictActivityIntent.create()
                        .cityId(item.getString(TPcd.id))
                        .province(mProvince)
                        .city(item.getString(TPcd.name))
                        .pcdDesc(getLocation())
//                        .place(mPlace)
                        .start(this);
            } else {
                Place place = makePlace(item.getString(TPcd.name));
                if (Profile.inst().isLogin()) {
                    refresh(RefreshWay.dialog);
                    exeNetworkReq(KIdCommit, NetFactory.newModifyBuilder()
                            .province(place.getString(TPlace.province))
                            .city(place.getString(TPlace.city))
                            .area(place.getString(TPlace.district))
                            .build());
                }else {
                    notify(NotifyType.province_finish, place);

                    Intent intent = new Intent()
                            .putExtra(yy.doctor.Extra.KData, place);
                    setResult(RESULT_OK, intent);
                    finish();
                }
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

                Profile.inst().put(place);
                Profile.inst().saveToSp();

                stopRefresh();

                notify(NotifyType.province_finish, place);
                finish();
            } else {
                onNetworkError(id, r.getError());
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
        mDistrict = null;
        if (TextUtil.isEmpty(mCity)) {
            mCity = name;
        } else {
            mDistrict = name;
        }

        mPlace = new Place();
        mPlace.put(TPlace.province, mProvince);
        mPlace.put(TPlace.city, mCity);
        mPlace.put(TPlace.district, mDistrict);

        return mPlace;
    }
}

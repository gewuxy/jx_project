package yy.doctor.ui.activity.register;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.ys.util.permission.Permission;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.dialog.BaseHintDialog;
import yy.doctor.model.Provinces;
import yy.doctor.model.Provinces.TProvinces;
import yy.doctor.network.NetFactory;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class ProvinceActivity extends BasePlaceActivity {

    private static final int KPermissionCodeLocation = 10;
    public static String mLocation;

    private OnLocationNotify mObserver;
    private View mLocationLayout;
    private ImageView mIvLocation;
    private AnimationDrawable mAnimation;
    private TextView mTvLocation;
    private TextView mTvLocationFailure;

    private String mLocationProvince;
    private String mLocationCity;
    private String mLocationArea;
    private BaseHintDialog mDialog;

    @Override
    public void initData() {
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
    public void setViews() {
        super.setViews();

        setAutoLoadMoreEnabled(false);
        //显示定位中布局
        showView(mLocationLayout);
        mIvLocation.setImageResource(R.drawable.province_location_anim);
        mAnimation = (AnimationDrawable) mIvLocation.getDrawable();
        mAnimation.start();

        //检查有没有定位权限   没有的话直接弹dialog
        if (checkPermission(KPermissionCodeLocation, Permission.location)) {
            location();
        } else {
            //停止动画 隐藏定位中布局  显示无法定位布局  显示dialog
            mAnimation.stop();
            goneView(mLocationLayout);
            showView(mTvLocationFailure);
            showLocDialog();
        }

        //item点击事件
        setOnAdapterClickListener((position, v) -> {
            Provinces province = getItem(position);
            CityActivity.nav(this, province.getString(TProvinces.id), province.getString(TProvinces.name));
        });
    }

    private void showLocDialog() {
        if (mDialog == null) {
            mDialog = new BaseHintDialog(ProvinceActivity.this);
            mDialog.addHintView(inflate(R.layout.dialog_locate_fail));
            mDialog.addButton(getString(R.string.know), "#0682e6", v -> mDialog.dismiss());
        }
        mDialog.show();
    }

    //定位
    private void location() {
        Location.inst().start();
        mObserver = (isSuccess, gps) -> {
            //返回主线程更新ui
            runOnUIThread(() -> {
                //停止动画 隐藏定位中布局
                mAnimation.stop();
                goneView(mLocationLayout);
                if (isSuccess) {
                    //定位成功
                    goneView(mTvLocationFailure);
                    Place place = (Place) gps.getObject(TGps.place);
                    mLocationProvince = place.getString(TPlace.province);
                    mLocationCity = place.getString(TPlace.city);
                    mLocationArea = place.getString(TPlace.district);
                    mLocation = mLocationProvince + " " + mLocationCity + " " + mLocationArea;
                    mTvLocation.setText(mLocationProvince + " " + mLocationCity + " " + mLocationArea);
                } else {
                    //定位失败  显示dialog
                    showView(mTvLocationFailure);
                    //YSLog.d("Gps", "失败");
                    mLocation = null;

                    showLocDialog();
                }
                LocationNotifier.inst().remove(mObserver);
                Location.inst().onDestroy();
            });
        };
        LocationNotifier.inst().add(mObserver);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.province());
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.province_finish) {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

}

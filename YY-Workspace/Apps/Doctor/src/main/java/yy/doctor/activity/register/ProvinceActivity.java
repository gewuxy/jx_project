package yy.doctor.activity.register;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.bd.location.Gps;
import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.ProvinceAdapter;
import yy.doctor.dialog.HintDialogSec;
import yy.doctor.model.Province;
import yy.doctor.model.Province.TProvince;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class ProvinceActivity extends BaseSRListActivity<Province, ProvinceAdapter> {

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
    private String mProvince;
    private String mCity;
    private String mArea;
    private HintDialogSec mDialog;

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "省市", this);
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_province_city_area_header);
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
        //mTvLocation.setText(mLocationProvince +" " + mLocationCity + " " + mLocationArea);
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
            Province province = getItem(position);
            CityActivity.nav(this, province.getString(TProvince.id), province.getString(TProvince.name));
        });
    }

    private void showLocDialog() {
        if (mDialog == null) {
            mDialog = new HintDialogSec(ProvinceActivity.this);
            mDialog.addButton("知道了", "#0682e6", v -> mDialog.dismiss());

            mDialog.setMainHint("请在系统设置中，打开“隐私-定位服务");
            mDialog.setSecHint("并允许定位服务");
        }
        mDialog.show();
    }

    //定位
    private void location() {
        Location.inst().start();
        mObserver = new OnLocationNotify() {

            @Override
            public void onLocationResult(boolean isSuccess, Gps gps) {
                //返回主线程更新ui
                runOnUIThread(new Runnable() {

                    @Override
                    public void run() {
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
                            //LogMgr.d("Gps", "失败");
                            mLocation = null;

                            showLocDialog();
                        }
                        LocationNotifier.inst().remove(mObserver);
                        Location.inst().stop();
                        Location.inst().onDestroy();
                    }
                });
            }
        };
        LocationNotifier.inst().add(mObserver);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.province());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mProvince = data.getStringExtra(Extra.KProvince);
            mCity = data.getStringExtra(Extra.KCity);
            mArea = data.getStringExtra(Extra.KArea);
            Intent i = new Intent();
            i.putExtra(Extra.KProvince, mProvince);
            i.putExtra(Extra.KCity, mCity);
            if (mArea != null) {
                i.putExtra(Extra.KArea, mArea);
            }
            setResult(RESULT_OK, i);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}

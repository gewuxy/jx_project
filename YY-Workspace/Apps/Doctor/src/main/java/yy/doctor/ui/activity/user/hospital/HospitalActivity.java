package yy.doctor.ui.activity.user.hospital;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lib.bd.location.Gps;
import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.ListResult;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.LevelDialog;
import yy.doctor.dialog.LevelDialog.OnLevelCheckChangeListener;
import yy.doctor.dialog.LocateErrDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.model.hospital.Hospital.THospital;
import yy.doctor.model.hospital.HospitalLevel;
import yy.doctor.model.hospital.HospitalLevel.THospitalLevel;
import yy.doctor.model.hospital.HospitalTitle;
import yy.doctor.model.hospital.HospitalTitle.TText;
import yy.doctor.model.hospital.IHospital;
import yy.doctor.model.hospital.IHospital.HospitalType;
import yy.doctor.network.NetworkAPISetter.UserAPI;
import yy.doctor.ui.activity.user.hospital.SearchHospitalActivity.Hos;
import yy.doctor.util.Util;

/**
 * @auther WangLan
 * @since 2017/7/19
 */

public class HospitalActivity extends BaseHospitalActivity
        implements OnGetPoiSearchResultListener, OnLocationNotify, OnLevelCheckChangeListener {

    private int KIdHospital = 0;
    private int KIdSave = 1;

    private final int KLimit = 12;
    private double mLocLon;
    private double mLocLat;

    private LatLng mLatLng;
    private PoiSearch mSearch;

    private LocateErrDialog mDialog;
    private LevelDialog mLevelDialog;

    private LinearLayout mHospitalSearch;

    private IHospital mCheckItem;
    private HospitalLevel mHospitalLevel;

    private boolean mIsFirst = true;
    private boolean mLocationAgain;
    private boolean mIsShow; // 当前界面( 百度定位的回调会回调到这)

    @Override
    public void initData() {
        mSearch = PoiSearch.newInstance();
        mIsShow = true;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.hospital, this);
    }

    @Nullable
    @Override
    public View createHeaderView() {
        return inflate(R.layout.activity_hospital_header);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mHospitalSearch);

        LocationNotifier.inst().add(this);
        //检查有没有定位权限
        if (checkPermission(0, Permission.location)) {
            Location.inst().start();
        }

        //POI检索的监听对象
        mSearch.setOnGetPoiSearchResultListener(this);
        setViewState(ViewState.loading);

        if (DeviceUtil.isNetworkEnabled()) {
            mLocationAgain = false;
        } else {
            mLocationAgain = true;
        }
    }

    @Override
    public boolean useErrorView() {
        return false;
    }

    @Override
    public boolean enableInitRefresh() {
        return false;
    }

    @Override
    public void swipeRefresh() {
        Location.inst().start();
    }

    @Override
    public void findViews() {
        super.findViews();
        mHospitalSearch = findView(R.id.hospital_search);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hospital_search: {
                startActivity(SearchHospitalActivity.class);
            }
            break;
        }
    }

    /**
     * 下拉刷新的动作
     */
    @Override
    public void onSwipeRefreshAction() {
        mIsFirst = true;
    }

    @Override
    public void getDataFromNet() {
        if (!DeviceUtil.isNetworkEnabled()){
            // 不影响mLocationAgain(无网到有网)
            stopRefresh();
            return;
        }
        if (mLatLng == null ) {
            // 定位失败(之前无网)
            if (mLocationAgain) {
                mLocationAgain = false;
                Location.inst().start();
            } else {
                // 有网, 不需要重新定位
                stopRefresh();
            }
        }else {
            netWork();
        }
    }

    /**
     * 发起附近检索请求
     */
    private void netWork() {
        //获得Key
        String key = "医院";
        //周边检索

        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .location(mLatLng)
                .pageCapacity(getLimit())    //每页条数
                .keyword(key)
                .radius(10000)// 检索半径，单位是米
                .pageNum(getOffset())
                .sortType(PoiSortType.distance_from_near_to_far);//由近到远排序
        mSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    @Override
    public void onItemClick(View v, int position) {
        mCheckItem = getItem(position);
        if (mCheckItem.getType() != HospitalType.hospital_title) {
            mLevelDialog = new LevelDialog(this);
            mLevelDialog.setListener(HospitalActivity.this);
            mLevelDialog.show();
        }
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                Location.inst().start();
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                onLocationError();
            }
            break;
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        ListResult<IHospital> r = new ListResult<>();

        if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {
            //如果搜索到的结果不为空，并且没有错误
            BigDecimal distance;
            List<IHospital> hospitals = new ArrayList<>();
            List<IHospital> data = new ArrayList<>();

            for (PoiInfo info : poiResult.getAllPoi()) {
                distance = new BigDecimal(getDistance(mLocLon, mLocLat, info.location.longitude, info.location.latitude))
                        .setScale(0, BigDecimal.ROUND_HALF_UP);//距离四舍五入

                Hospital hospital = new Hospital();
                hospital.put(THospital.name, info.name);
                hospital.put(THospital.address, info.address);
                hospital.put(THospital.distance, distance);
                hospitals.add(hospital);
            }

            if (mIsFirst) {
                if (hospitals.isEmpty()) {
                    // 没有结果
                    r.setCode(ErrorCode.KUnKnow);
                    r.setMessage("搜索不到你需要的信息");
                } else {
                    // 数据展示
                    HospitalTitle title = new HospitalTitle();
                    title.put(TText.name, "推荐医院");
                    data.add(title);// position = 0

                    data.add(hospitals.get(0)); //第一条数据, position = 1

                    title = new HospitalTitle();
                    title.put(TText.name, "附近医院");
                    data.add(title);// position = 2

                    for (int i = 1; i < hospitals.size(); i++) {
                        data.add(hospitals.get(i));
                    }

                    r.setCode(ErrorCode.KOk);
                    r.setData(data);
                }
                mIsFirst = false;
            } else {
                r.setCode(ErrorCode.KOk);
                r.setData(hospitals);
            }
        } else {
            r.setCode(ErrorCode.KUnKnow);
            r.setMessage("搜索不到你需要的信息");
        }

        onNetworkSuccess(KIdHospital, r);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        //获得POI室内检索结果
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        YSLog.d(TAG,"---");
        Location.inst().start();
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.hospital_finish) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsShow = false;
    }

    @Override
    public void onLocationResult(boolean isSuccess, Gps gps) {
        if (!mIsShow) {
            return;
        }
        Location.inst().stop();
        if (isSuccess) {
                //定位成功
                mLocLon = Double.parseDouble(gps.getString(TGps.longitude));
                mLocLat = Double.parseDouble(gps.getString(TGps.latitude));
                mLatLng = new LatLng(mLocLat, mLocLon);

                getDataFromNet();
                YSLog.d(TAG, "onLocationResult:====" + getOffset() + mLocLon);
                YSLog.d(TAG, "onLocationResult:" + getOffset() + mLocLat);
        } else {
            //定位失败  显示dialog
            YSLog.d("Gps", "失败");
            onLocationError();
        }
    }

    /**
     * 初始化Dialog
     */
    private void onLocationError() {

        Location.inst().stop();
        if (!DeviceUtil.isNetworkEnabled()) {
            showToast(R.string.network_disabled);
        } else {
            //有网但是定位失败  显示dialog
            if (mDialog == null) {
                mDialog = new LocateErrDialog(this);
                mDialog.show();
            }
        }
        simulateSuccess(KIdHospital);
    }

    @Override
    public int getLimit() {
        return KLimit;
    }

    @Override
    public int getInitOffset() {
        return 0;
    }

    @Override
    public void onLevelChecked(HospitalLevel h) {
        if (mLevelDialog != null) {
            mLevelDialog.dismiss();
        }
        if (mCheckItem instanceof Hospital) {
            Hospital hospital = (Hospital) mCheckItem;
            mHospitalLevel = h;
            if (Profile.inst().isLogin()) {
                refresh(RefreshWay.dialog);
                exeNetworkReq(KIdSave, UserAPI.modify()
                        .hospital(hospital.getString(THospital.name))
                        .hospitalLevel(String.valueOf(h.getInt(THospitalLevel.id)))
                        .build());
            } else {
                Hos hos = new Hos();
                hos.mName = hospital.getString(THospital.name);
                hos.mHospitalLevel = mHospitalLevel;
                Intent intent = new Intent().putExtra(Extra.KData, hos);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdSave) {
            ListResult res = (ListResult) result;
            if (res.isSucceed()) {

                String name = ((Hospital) mCheckItem).getString(THospital.name);
                Profile.inst().put(TProfile.hospital, name);
                Profile.inst().put(TProfile.systemProperties, mHospitalLevel);
                Profile.inst().saveToSp();

                Hos hos = new Hos();
                hos.mName = name;
                hos.mHospitalLevel = mHospitalLevel;
                Intent i = new Intent().putExtra(Extra.KData, hos);
                setResult(RESULT_OK, i);
                showToast(R.string.user_save_success);
                finish();
            } else {
                onNetworkError(id, res.getError());
                stopRefresh();
            }
        } else {
            super.onNetworkSuccess(id, result);
        }
    }

    /**
     * 计算两点之间真实距离
     *
     * @return 米
     */
    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        // 维度
        double lat1 = (Math.PI / 180) * latitude1;
        double lat2 = (Math.PI / 180) * latitude2;

        // 经度
        double lon1 = (Math.PI / 180) * longitude1;
        double lon2 = (Math.PI / 180) * longitude2;

        // 地球半径
        double R = 6371;

        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        return d * 1000;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLevelDialog != null) {
            mLevelDialog.dismiss();
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
        LocationNotifier.inst().remove(this);
        Location.inst().onDestroy();
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_empty_footer_locate_err);
    }
}

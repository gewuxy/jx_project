package yy.doctor.ui.activity.register;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
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
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.ListResult;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.dialog.BaseHintDialog;
import yy.doctor.dialog.LevelDialog;
import yy.doctor.dialog.LevelDialog.OnLevelCheckListener;
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
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.search.SearchHospitalActivity;
import yy.doctor.ui.activity.search.SearchHospitalActivity.Hos;
import yy.doctor.util.Util;

/**
 * @auther WangLan
 * @since 2017/7/19
 */

public class HospitalActivity extends BaseSRListActivity<IHospital, HospitalAdapter> implements OnGetPoiSearchResultListener, OnLocationNotify, OnLevelCheckListener {

    private int KIdHospital = 0;
    private int KIdSave = 1;

    private final int KLimit = 12;
    private double mLocLon;
    private double mLocLat;

    private LatLng mLatLng;
    private PoiSearch mSearch;
    private BaseHintDialog mDialog;

    private LinearLayout mHospitalSearch;

    private boolean mIsFirst = true;
    private LevelDialog mLevelDialog;
    private IHospital mCheckItem;
    private HospitalLevel mHospitalLevel;

    @Override
    public void initData() {
        mSearch = PoiSearch.newInstance();
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

        //检查有没有定位权限   没有的话直接弹dialog
        if (checkPermission(0, Permission.location)) {
            Location.inst().start();
        } else {
            onLocationError();
        }

        LocationNotifier.inst().add(this);
        //POI检索的监听对象
        mSearch.setOnGetPoiSearchResultListener(this);
        setViewState(ViewState.loading);

        setRefreshEnabled(false);
//        setAutoLoadMoreEnabled(true);
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

    @Override
    public void getDataFromNet() {
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

        onNetworkSuccess(0, r);

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        //获得POI的详细检索结果，如果发起的是详细检索，这个方法会得到回调(需要uid)
        //详细检索一般用于单个地点的搜索，比如搜索一大堆信息后，选择其中一个地点再使用详细检索
        if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {

            showToast("抱歉，未找到结果");
        } else {
            // 正常返回结果的时候，此处可以获得很多相关信息
            showToast(poiDetailResult.getName() + ": " + poiDetailResult.getAddress());
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        //获得POI室内检索结果
    }

    /**
     * 初始化Dialog
     */
    private void onLocationError() {
        YSLog.d("Gps", "失败");
        if (!DeviceUtil.isNetworkEnabled()) {
            showToast("当前网络不可用, 不可定位");
        } else {
            //有网但是定位失败  显示dialog
            mDialog = new BaseHintDialog(this);
            mDialog.addHintView(inflate(R.layout.dialog_locate_fail));
            mDialog.addButton("取消", v -> mDialog.dismiss());
            mDialog.addButton("去设置", v -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent,0);
            });
            mDialog.show();
        }

        // 模拟成功无数据， 显示empty footer view
        ListResult<IHospital> r = new ListResult<>();
        r.setCode(ErrorCode.KOk);
        List<IHospital> hospitals = new ArrayList<>();
        r.setData(hospitals);
        onNetworkSuccess(KIdHospital, r);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.hospital_finish) {
            Hos hos = (Hos) data;
            String hosName = hos.mName;
            mHospitalLevel = hos.mHospitalLevel;
            int HosLvId = mHospitalLevel.getInt(THospitalLevel.id);
            Profile.inst().put(TProfile.hospital, hosName);
            Profile.inst().put(TProfile.systemProperties, mHospitalLevel);
            Profile.inst().saveToSp();
            if (Profile.inst().isLogin()) {
                exeNetworkReq(KIdSave, NetFactory.newModifyBuilder().hospital(hosName).hospitalLevel(HosLvId).build());
            }
            Intent intent = new Intent().putExtra(Extra.KData, mHospitalLevel);
            setResult(RESULT_OK, intent);
            finish();
        }
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
    public void onLocationResult(boolean isSuccess, Gps gps) {
        if (isSuccess) {
            //定位成功
            mLocLon = Double.parseDouble(gps.getString(TGps.longitude));
            mLocLat = Double.parseDouble(gps.getString(TGps.latitude));
            mLatLng = new LatLng(mLocLat, mLocLon);

            getDataFromNet();
        } else {
            //定位失败  显示dialog
            // FIXME: 失败
            YSLog.d("Gps", "失败");
            onLocationError();
        }
        stopLocation();
    }

    private void stopLocation() {
        LocationNotifier.inst().remove(this);
        Location.inst().stop();
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
            int levelId = h.getInt(THospitalLevel.id);
            String name = hospital.getString(THospital.name);
            mHospitalLevel = h;

            Profile.inst().put(TProfile.hospital, name);
            Profile.inst().put(TProfile.systemProperties, mHospitalLevel);
            Profile.inst().saveToSp();

            if (Profile.inst().isLogin()) {
                exeNetworkReq(KIdSave, NetFactory.newModifyBuilder().hospital(name).hospitalLevel(levelId).build());
            } else {
                Intent intent = new Intent().putExtra(Extra.KData, mHospitalLevel);
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
                Intent i = new Intent().putExtra(Extra.KData, mHospitalLevel);
                setResult(RESULT_OK, i);
                finish();
            } else {
                onNetworkError(id, res.getError());
                stopRefresh();
            }
        } else {
            super.onNetworkSuccess(id, result);
        }
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_locate_fail_empty_footer);
    }

    @Override
    protected String getEmptyText() {
        return "无法获取您的位置信息";
    }
}

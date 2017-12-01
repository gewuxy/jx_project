package jx.doctor.ui.activity.user.hospital;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import lib.bd.location.Gps;
import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseSRListActivity;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.adapter.user.HospitalAdapter;
import jx.doctor.dialog.LevelDialog;
import jx.doctor.dialog.LevelDialog.OnLevelCheckChangeListener;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.model.hospital.Hospital;
import jx.doctor.model.hospital.Hospital.THospital;
import jx.doctor.model.hospital.HospitalLevel;
import jx.doctor.model.hospital.HospitalLevel.THospitalLevel;
import jx.doctor.model.hospital.HospitalName;
import jx.doctor.model.hospital.HospitalName.THospitalName;
import jx.doctor.model.hospital.IHospital;
import jx.doctor.model.hospital.IHospital.HospitalType;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor;
import jx.doctor.util.Util;

/**
 * 医院基类
 *
 * @auther yuansui
 * @since 2017/8/11
 */
abstract public class BaseHospitalActivity extends BaseSRListActivity<IHospital, HospitalAdapter> implements
        OnLocationNotify,
        OnGetPoiSearchResultListener,
        OnLevelCheckChangeListener, OnAdapterClickListener {

    protected final String KHospital = ResLoader.getString(R.string.hospital);

    protected final int KIdHospital = 0;
    private final int KIdSave = 1;
    private final int KLimit = 12; // 每页展示的数据

    protected HospitalName mHospitalName; // 点击的dialog的Item(包括医院名字)
    protected LatLng mLatLng; // 定位信息
    protected boolean mFirstAction; // 第一次需要的操作

    int mFromType; // 从哪里来
    private PoiSearch mSearch; // 搜索

    protected TextView mTvEmpty;

    @IntDef({
            FromType.register,
            FromType.profile,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FromType {
        int register = 0;
        int profile = 1;
    }

    @CallSuper
    @Override
    public void initData(Bundle state) {
        mFromType = getIntent().getIntExtra(Extra.KData, FromType.register);
        mFirstAction = true;
        mSearch = PoiSearch.newInstance();
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_empty_footer_locate_err);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvEmpty = findView(R.id.hospital_empty_tv);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        LocationNotifier.inst().add(this);
        startLocation();

        // POI检索的监听对象
        mSearch.setOnGetPoiSearchResultListener(this);

        getAdapter().setOnAdapterClickListener(this);
    }

    @Override
    public boolean enableInitRefresh() {
        return false;
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                startLocation();
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                noLocationPermission();
            }
            break;
        }
    }

    @Override
    public void onLocationResult(boolean isSuccess, Gps gps) {
        if (isSuccess) {
            //定位成功
            double lon = Double.parseDouble(gps.getString(TGps.longitude));
            double lat = Double.parseDouble(gps.getString(TGps.latitude));
            YSLog.d(TAG, "onLocationResult:longitude" + lon);
            YSLog.d(TAG, "onLocationResult:latitude" + lat);
            mLatLng = new LatLng(lat, lon);

            LocationNotifier.inst().remove(this);
            locationSuccess();
        } else {
            //定位失败  显示dialog
            YSLog.d("Gps", "失败");
            noLocationPermission();
        }
        Location.inst().stop();
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        Result<IHospital> r = new Result<>();

        if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {
            //如果搜索到的结果不为空，并且没有错误
            List<PoiInfo> info = poiResult.getAllPoi();

            if (info == null || info.size() == 0) {
                // 没有结果
                r.setCode(ErrorCode.KUnknown);
                r.setMessage("搜索不到你需要的信息");
            } else {
                // 有结果
                searchSuccess(info, r);
            }
        } else {
            searchError(r);
        }

        onNetworkSuccess(KIdHospital, r);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        // 获得POI室内检索结果
    }

    @Override
    public void onAdapterClick(int position, View v) {
        IHospital item = getItem(position);
        if (item.getType() == HospitalType.hospital_data) {
            chooseLevel(((Hospital) item).getString(THospital.name));
        }
    }

    @Override
    public final void onLevelChecked(HospitalLevel h) {
        mHospitalName.put(THospitalName.level, h);
        if (mFromType == FromType.profile) {
            showLoadingDialog();
            exeNetworkReq(KIdSave, NetworkApiDescriptor.UserAPI.modify()
                    .hospital(mHospitalName.getString(THospitalName.name))
                    .hospitalLevel(h.getString(THospitalLevel.id))
                    .build());
        } else {
            returnResult();
            finish();
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (KIdSave == id) {
            return JsonParser.error(resp.getText());
        } else {
            return super.onNetworkResponse(id, resp);
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KIdSave) {
            stopDialogRefresh();
            if (r.isSucceed()) {

                Profile.inst().put(TProfile.hospital, mHospitalName.getString(THospitalName.name));
                Profile.inst().put(TProfile.systemProperties, mHospitalName.get(THospitalName.level));
                Profile.inst().saveToSp();

                returnResult();
                showToast(R.string.user_save_success);
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            super.onNetworkSuccess(id, r);
        }
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
    protected void onDestroy() {
        super.onDestroy();

        mSearch.destroy();
        LocationNotifier.inst().remove(this);
        Location.inst().onDestroy();
    }

    /**
     * 检查有没有定位权限
     */
    protected void startLocation() {
        if (checkPermission(0, Permission.location) && !Util.noNetwork()) {
            Location.inst().start();
        }
    }

    /**
     * 发起附近检索请求
     */
    protected void searchHospital(String key) {
        //周边检索
        PoiNearbySearchOption option = new PoiNearbySearchOption()
                .location(mLatLng)
                .pageCapacity(getLimit())    //每页条数
                .keyword(key)
                .radius(getDistance())// 检索半径，单位是米
                .pageNum(getOffset())
                .sortType(PoiSortType.distance_from_near_to_far);//由近到远排序
        mSearch.searchNearby(option);// 发起附近检索请求
    }

    /**
     * 模拟网络成功, 显示empty footer view
     */
    protected void simulateSuccess() {
        Result<IHospital> r = new Result<>();
        r.setCode(ErrorCode.KOk);
        List<IHospital> hospitals = new ArrayList<>();
        r.setData(hospitals);
        onNetworkSuccess(KIdHospital, r);
    }

    /**
     * 医院数据的封装
     */
    protected Hospital convertToHospital(@NonNull PoiInfo info) {
        Hospital hospital = new Hospital();
        hospital.put(THospital.name, info.name);
        hospital.put(THospital.address, info.address);
        hospital.put(THospital.distance, getDistance(info.location));
        return hospital;
    }

    /**
     * 计算两点之间真实距离
     *
     * @return 米
     */
    protected int getDistance(LatLng latLng) {
        double p = Math.PI / 180;
        // 维度
        double lat1 = p * mLatLng.latitude;
        double lat2 = p * latLng.latitude;
        // 经度
        double lon1 = p * mLatLng.longitude;
        double lon2 = p * latLng.longitude;
        // 地球半径
        int R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        return (int) (d * 1000);
    }

    /**
     * 选择医院等级(记录选择的医院)
     */
    protected void chooseLevel(String name) {
        mHospitalName = new HospitalName();
        mHospitalName.put(THospitalName.name, name);
        LevelDialog dialog = new LevelDialog(this);
        dialog.setListener(this);
        dialog.show();
    }

    /**
     * 定位成功
     */
    abstract protected void locationSuccess();

    /**
     * 没有定位权限时
     */
    abstract protected void noLocationPermission();

    /**
     * 获取搜索范围
     */
    abstract protected int getDistance();

    /**
     * 搜索到结果
     */
    abstract protected void searchSuccess(List<PoiInfo> info, Result<IHospital> r);

    /**
     * 没搜索到结果
     */
    abstract protected  void searchError(Result<IHospital> r);

    /**
     * 选择医院返回结果
     */
    abstract protected void returnResult();

}

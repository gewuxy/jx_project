package yy.doctor.ui.activity.user.hospital;

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;

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
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.ListResult;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.dialog.LevelDialog;
import yy.doctor.dialog.LevelDialog.OnLevelCheckChangeListener;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.model.hospital.Hospital.THospital;
import yy.doctor.model.hospital.HospitalLevel;
import yy.doctor.model.hospital.HospitalLevel.THospitalLevel;
import yy.doctor.model.hospital.HospitalName;
import yy.doctor.model.hospital.HospitalName.THospitalName;
import yy.doctor.model.hospital.IHospital;
import yy.doctor.model.hospital.IHospital.HospitalType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter;

/**
 * 医院基类
 *
 * @auther yuansui
 * @since 2017/8/11
 */
abstract public class BaseHospitalActivity extends BaseSRListActivity<IHospital, HospitalAdapter> implements
        OnLocationNotify,
        OnGetPoiSearchResultListener,
        OnLevelCheckChangeListener {

    protected final int KIdHospital = 0;
    protected final int KIdSave = 1;
    protected final String KHospital = ResLoader.getString(R.string.hospital);

    private final int KLimit = 12;

    private int mFromType; // 从哪里来

    protected HospitalName mHospitalName; // 点击的dialog的Item(包括医院名字)
    protected LatLng mLatLng; // 定位信息
    private PoiSearch mSearch; // 搜索

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
    public void initData() {
        mSearch = PoiSearch.newInstance();
        mFromType = getIntent().getIntExtra(Extra.KData, FromType.register);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        LocationNotifier.inst().add(this);
        // 检查有没有定位权限
        if (checkPermission(0, Permission.location)) {
            Location.inst().start();
        }

        // POI检索的监听对象
        mSearch.setOnGetPoiSearchResultListener(this);
    }

    @Override
    public boolean enableInitRefresh() {
        return false;
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
        ListResult<IHospital> r = new ListResult<>();

        if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {
            //如果搜索到的结果不为空，并且没有错误
            List<PoiInfo> info = poiResult.getAllPoi();

            if (info == null || info.size() == 0) {
                // 没有结果
                r.setCode(ErrorCode.KUnKnow);
                r.setMessage("搜索不到你需要的信息");
            } else {
                // 有结果
                searchSuccess(info, r);
            }
        } else {
            searchError(r);
        }

        super.onNetworkSuccess(KIdHospital, r);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        // 获得POI室内检索结果
    }

    @Override
    public final void onItemClick(View v, int position) {
        IHospital item = getItem(position);
        if (item.getType() == HospitalType.hospital_data) {
            chooseLevel(((Hospital) item).getString(THospital.name));
        }
    }

    @Override
    public final void onLevelChecked(HospitalLevel h) {
        mHospitalName.put(THospitalName.level, h);
        if (mFromType == FromType.profile) {
            refresh(RefreshWay.dialog);
            exeNetworkReq(KIdSave, NetworkAPISetter.UserAPI.modify()
                    .hospital(mHospitalName.getString(THospitalName.name))
                    .hospitalLevel(h.getString(THospitalLevel.id))
                    .build());
        } else {
            returnResult();
            finish();
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (KIdSave == id) {
            return JsonParser.error(r.getText());
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdSave) {
//            stopRefresh();
            Result r = (Result) result;
            if (r.isSucceed()) {

                if (mFromType == FromType.profile) {
                    Profile.inst().put(TProfile.hospital, mHospitalName.getString(THospitalName.name));
                    Profile.inst().put(TProfile.systemProperties, mHospitalName.getEv(THospitalName.level));
                    Profile.inst().saveToSp();
                }

                returnResult();
                showToast(R.string.user_save_success);
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            super.onNetworkSuccess(id, result);
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
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_empty_footer_locate_err);
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
                .radius(10000)// 检索半径，单位是米
                .pageNum(getOffset())
                .sortType(PoiSortType.distance_from_near_to_far);//由近到远排序
        mSearch.searchNearby(option);// 发起附近检索请求
    }

    /**
     * 模拟网络成功, 显示empty footer view
     *
     * @param reqId
     */
    protected void simulateSuccess(int reqId) {
        ListResult<IHospital> r = new ListResult<>();
        r.setCode(ErrorCode.KOk);
        List<IHospital> hospitals = new ArrayList<>();
        r.setData(hospitals);
        onNetworkSuccess(reqId, r);
    }

    /**
     * 医院数据的封装
     */
    protected Hospital convertToHospital(@NonNull PoiInfo info) {
        Hospital hospital = new Hospital();
        hospital.put(THospital.name, info.name);
        hospital.put(THospital.address, info.address);
        hospital.put(THospital.distance, getDistance(mLatLng.longitude, mLatLng.latitude, info.location.longitude, info.location.latitude));
        return hospital;
    }

    /**
     * 计算两点之间真实距离
     *
     * @return 米
     */
    protected int getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocationNotifier.inst().remove(this);
        Location.inst().onDestroy();
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
     * 搜索到结果
     */
    abstract protected void searchSuccess(List<PoiInfo> info, ListResult<IHospital> r);

    /**
     * 没搜索到结果
     */
    protected abstract void searchError(ListResult<IHospital> r);

    /**
     * 选择医院返回结果
     */
    abstract protected void returnResult();

}

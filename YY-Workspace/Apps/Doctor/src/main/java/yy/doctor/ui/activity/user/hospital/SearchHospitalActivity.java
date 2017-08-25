package yy.doctor.ui.activity.user.hospital;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

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
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.ListResult;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.dialog.HintDialog;
import yy.doctor.dialog.LevelDialog;
import yy.doctor.dialog.LevelDialog.OnLevelCheckListener;
import yy.doctor.model.Profile;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.model.hospital.Hospital.THospital;
import yy.doctor.model.hospital.HospitalLevel;
import yy.doctor.model.hospital.IHospital;
import yy.doctor.model.hospital.IHospital.HospitalType;
import yy.doctor.util.Util;

/**
 * 医院搜索
 *
 * @auther WangLan
 * @since 2017/7/20
 */
public class SearchHospitalActivity extends BaseHospitalActivity
        implements OnGetPoiSearchResultListener, OnLocationNotify, OnLevelCheckListener, OnClickListener {

    private final int KLimit = 12;

    private EditText mEtSearch;

    private HintDialog mDialog;
    private LevelDialog mDialogLevel;

    private PoiSearch mSearch;
    private double mLocLon;
    private double mLocLat;
    private LatLng mLatLng;
    private String mStrSearch;
    private IHospital mCheckItem; // 点击的医院
    private boolean mLocationAgain; // 需要再次定位;
    private boolean mFindHospital = true; //找到医院默默认为true，找不到为false；
    private OnClickListener mSearchListener;

    @Override
    public void initData() {
        mSearch = PoiSearch.newInstance();
        mSearchListener = v -> {
            // KeyboardUtil.hideFromView(v);
            mStrSearch = mEtSearch.getText().toString().trim();
            if (TextUtil.isEmpty(mStrSearch)) {
                showToast("请输入搜索内容");
                return;
            }
            if (Util.noNetwork()) {
                return;
            }
            if (mLocationAgain) {
                Location.inst().start();
                return;
            }
            if (mLatLng == null) {
                // 定位失败
                onLocationError();
                showToast("无法获取您的位置信息");
            } else {
                refresh();
                YSLog.d(TAG, "offset = " + getOffset());
            }
        };
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);

        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        mEtSearch.setHint("搜索医院");
        bar.addViewMid(view, null);

        // KeyboardUtil.hideFromView(v);
        bar.addTextViewRight("搜索", mSearchListener);
    }

    @Override
    public boolean enableInitRefresh() {
        return false;
    }

    @Override
    public void setViews() {
        super.setViews();

        //检查有没有定位权限   没有的话直接弹dialog
        if (checkPermission(0, Permission.location)) {
            Location.inst().start();
        }

        if (DeviceUtil.isNetworkEnabled()) {
            mLocationAgain = false;
        } else {
            mLocationAgain = true;
        }

        LocationNotifier.inst().add(this);

        //POI检索的监听对象
        mSearch.setOnGetPoiSearchResultListener(this);

        setRefreshEnabled(false);
        setViewState(ViewState.normal);
    }

    @Override
    public void getDataFromNet() {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .location(mLatLng)
                .pageCapacity(KLimit)    //每页条数
                .keyword(mStrSearch + "医院")
                .radius(10000)
                .pageNum(getOffset())
                .sortType(PoiSortType.distance_from_near_to_far);//由近到远排序
        mSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);

        mCheckItem = getItem(position);
        if (mCheckItem.getType() != HospitalType.hospital_title) {
            mDialogLevel = new LevelDialog(this);
            mDialogLevel.setListener(SearchHospitalActivity.this);
            mDialogLevel.show();
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
        KeyboardUtil.hideFromView(getCurrentFocus());
        //removeAll();//清空列表
        ListResult<IHospital> r = new ListResult<>();
        if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {
            //如果搜索到的结果不为空，并且没有错误
            BigDecimal distance;
            List<IHospital> hospitals = new ArrayList<>();
            for (PoiInfo info : poiResult.getAllPoi()) {
                distance = new BigDecimal(getDistance(mLocLon, mLocLat, info.location.longitude, info.location.latitude))
                        .setScale(0, BigDecimal.ROUND_HALF_UP);//距离四舍五入

                Hospital hospital = new Hospital();
                hospital.put(THospital.name, info.name);
                hospital.put(THospital.address, info.address);
                hospital.put(THospital.distance, distance);
                hospitals.add(hospital);
            }
            if (hospitals.isEmpty()) {
                r.setCode(ErrorCode.KUnKnow);
                r.setMessage("没有数据，搜索不到你需要的信息");
            } else {
                r.setCode(ErrorCode.KOk);
                r.setData(hospitals);
            }
        } else {
            if (mDialog == null) {
                mDialog = new HintDialog(this);
                mDialog.addHintView(inflate(R.layout.dialog_find_hospital_fail));
                mDialog.addButton("取消", v -> mDialog.dismiss());
                mDialog.addButton("确定", v -> {
                    mDialogLevel = new LevelDialog(this);
                    mDialogLevel.setListener(SearchHospitalActivity.this);
                    mDialogLevel.show();
                });
            }
            mDialog.show();

            // 模拟成功无数据， 显示empty footer view
            r.setCode(ErrorCode.KOk);
            List<IHospital> hospitals = new ArrayList<>();
            r.setData(hospitals);

            mFindHospital = false;
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
        showToast("抱歉，未找到结果");
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

    /**
     * 初始化Dialog
     */
    private void onLocationError() {
        simulateSuccess(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
        }

        if (mDialogLevel != null) {
            mDialogLevel.dismiss();
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
            YSLog.d(TAG,"onLocationResult:"+ mLocLon);
            YSLog.d(TAG,"onLocationResult:"+ mLocLat);
            mLatLng = new LatLng(mLocLat, mLocLon);
            if (mLocationAgain) {
                mSearchListener.onClick(null);
            }
        } else {
            //定位失败  显示dialog
            YSLog.d("Gps", "失败");
            onLocationError();
        }

        if (DeviceUtil.isNetworkEnabled()) {
            mLocationAgain = false;
        }

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
        Hos hos = new Hos();
        if (mCheckItem == null && !mFindHospital) {
            Hospital hospital = new Hospital();
            hospital.put(THospital.name, mStrSearch);
            hos.mName = hospital.getString(THospital.name);
        } else {
            Hospital hospital2 = (Hospital) mCheckItem;
            hos.mName = hospital2.getString(THospital.name);
        }
        hos.mHospitalLevel = h;
        notify(NotifyType.hospital_finish, hos);

        if (Profile.inst().isLogin()) {
            showToast(R.string.user_save_success);
        }
        finish();
    }

    public class Hos {
        public String mName;
        public HospitalLevel mHospitalLevel;
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_empty_footer_locate_err);
    }
}

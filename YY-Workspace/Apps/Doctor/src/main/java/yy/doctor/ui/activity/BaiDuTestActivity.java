package yy.doctor.ui.activity;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.math.BigDecimal;
import java.util.List;

import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.MyOverLay;
import lib.bd.location.OnLocationNotify;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.dialog.BaseHintDialog;

/**
 * @auther WangLan
 * @since 2017/7/15
 */

public class BaiDuTestActivity extends BaseActivity {
    BaiduMap mBaiDuMap;
    PoiSearch search = PoiSearch.newInstance();
    EditText mEtCity;
    EditText mEtKey;
    Button mBdBtn;
    EditText mEtSd;
    Button mSdBtn;
    MapView mMapView;
    List<PoiInfo> poiItems;
    private double mSzLongitude;
    private double mSzLatitude;
    private final int KPermissionLocation = 1;
    private BaseHintDialog mDialog;
    private OnLocationNotify mObserver;
    private double mLocLon;
    private double mLocLat;

    @Override
    public void initData() {
        SDKInitializer.initialize(getApplicationContext());

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_baidu_test;

    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void findViews() {
        mEtCity = findView(R.id.et_city);
        mEtKey = findView(R.id.et_key);
        mBdBtn = findView(R.id.bd_btn);
        mEtSd = findView(R.id.et_sd);
        mSdBtn = findView(R.id.sd_btn);
        mMapView = findView(R.id.mapView);
    }

    @Override
    public void setViews() {
        setOnClickListener(mBdBtn);
        setOnClickListener(mSdBtn);
        mBaiDuMap = mMapView.getMap();
        //普通地图
        mBaiDuMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //检查有没有定位权限   没有的话直接弹dialog
        if (checkPermission(KPermissionLocation, Permission.location)) {
            location();
        } else {
            showLocationDialog();
        }


        //POI检索的监听对象
        OnGetPoiSearchResultListener resultListener = new OnGetPoiSearchResultListener() {
            //获得POI的检索结果，一般检索数据都是在这里获取
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //如果搜索到的结果不为空，并且没有错误
                if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {
                    MyOverLay overlay = new MyOverLay(mBaiDuMap, search);//这传入search对象，因为一般搜索到后，点击时方便发出详细搜索
                    //设置数据,这里只需要一步，
                    overlay.setData(poiResult);
                    //添加到地图
                    overlay.addToMap();
                    //将显示视图拉倒正好可以看到所有POI兴趣点的缩放等级
                    overlay.zoomToSpan();//计算工具
                    //设置标记物的点击监听事件
                    mBaiDuMap.setOnMarkerClickListener(overlay);
                    poiItems = poiResult.getAllPoi();
                  /*  for (int i = 0; i < 10 ; i++) {
                        PoiInfo info = poiItems.get(i);
                        String name =  info.name;
                        showToast(name.toString());
                   }
*/
                    double lon;
                    double lat;
                    StringBuffer sb = new StringBuffer();
                    for (PoiInfo info : poiResult.getAllPoi()) {
                        lon = info.location.longitude;
                        lat = info.location.latitude;
                        double distance = getDistance(mLocLon, mLocLat, lon, lat);
                        BigDecimal bigDecimal = new BigDecimal(distance).setScale(0, BigDecimal.ROUND_HALF_UP);//距离四舍五入
                        sb.append("名称：")
                                .append(info.name)
                                .append("\n地址:")
                                .append(info.address)
                                .append("\n距离：")
                                .append(bigDecimal)
                                .append("\n\n");
                    }
                    new AlertDialog
                            .Builder(BaiDuTestActivity.this)
                            .setTitle("搜索到的名称")
                            .setMessage(sb.toString())
                            .setPositiveButton("关闭", (dialog, which) -> dialog.dismiss()).show();

                    //showToast("搜索到了");
                } else {
                    //Toast.makeText(getApplication(), "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
                    showToast("搜索不到你需要的信息");
                }
            }

            //获得POI的详细检索结果，如果发起的是详细检索，这个方法会得到回调(需要uid)
            //详细检索一般用于单个地点的搜索，比如搜索一大堆信息后，选择其中一个地点再使用详细检索
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(getApplication(), "抱歉，未找到结果",
                            Toast.LENGTH_SHORT).show();
                } else {// 正常返回结果的时候，此处可以获得很多相关信息
                    Toast.makeText(getApplication(), poiDetailResult.getName() + ": "
                                    + poiDetailResult.getAddress(),
                            Toast.LENGTH_LONG).show();
                }
            }

            //获得POI室内检索结果
            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        };
        PoiSearch.newInstance().setOnGetPoiSearchResultListener(resultListener);


    }

    private void location() {
        mObserver = (isSuccess, gps) -> {
            //返回主线程更新ui
            runOnUIThread(() -> {
                if (isSuccess) {
                    //定位成功
                    mLocLon = Double.parseDouble(gps.getString(TGps.longitude));
                    mLocLat = Double.parseDouble(gps.getString(TGps.latitude));
                    showToast(mLocLon + "   " + mLocLat);
                } else {
                    //定位失败  显示dialog
                    //YSLog.d("Gps", "失败");
                    showLocationDialog();
                }
                LocationNotifier.inst().remove(mObserver);
                Location.inst().onDestroy();
            });
        };
        Location.inst().start();
        LocationNotifier.inst().add(mObserver);
    }

    /**
     * 初始化Dialog
     */
    private void showLocationDialog() {
        mDialog = new BaseHintDialog(this);
        mDialog.addHintView(inflate(R.layout.dialog_locate_fail));
        mDialog.addButton(getString(R.string.know), v -> mDialog.dismiss());
        mDialog.show();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bd_btn: {
                //城市搜索，需要城市名称和关键字
                String city = mEtCity.getText().toString();
                String key = mEtKey.getText().toString();
                //发起检索
                PoiCitySearchOption poiCity = new PoiCitySearchOption();
                poiCity.keyword(key).city(city);//这里还能设置显示的个数，默认显示10个
                PoiSearch.newInstance().searchInCity(poiCity);//执行搜索，搜索结束后，在搜索监听对象里面的方法会被回
            }
            break;
            case R.id.sd_btn: {
//                mSzLongitude = 113.943062;
//                mSzLatitude = 22.54069;

                //定义Maker坐标点,深圳大学经度和纬度113.943062,22.54069
                //设置的时候经纬度是反的 纬度在前，经度在后
                LatLng point = new LatLng(mLocLat, mLocLon);
//                LatLng point = new LatLng(113.943062, 22.54069);
                //获得Key
                String key = mEtSd.getText().toString();
                //String key = "饭店";
                //周边检索
                PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                        .location(point)
                        .pageCapacity(15)    //每页条数
                        .keyword(key)
                        .radius(1000)// 检索半径，单位是米
                        .pageNum(0);//搜索一页
                boolean b = PoiSearch.newInstance().searchNearby(nearbySearchOption);// 发起附近检索请求
                YSLog.d(TAG, "b = " + b);
            }
            break;
        }
    }

    /**
     * 71      * 补充：计算两点之间真实距离
     * 72      * @return 米
     * 73
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


}



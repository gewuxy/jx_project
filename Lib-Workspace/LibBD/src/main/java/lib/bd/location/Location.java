package lib.bd.location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import lib.bd.location.Gps.TGps;
import lib.ys.AppEx;
import lib.ys.LogMgr;

public class Location {

    private static final String TAG = Location.class.getSimpleName();

    private LocationClient mLocationClient;
    private MyLocationListener mListener;

    private static Location mInst = null;

    private boolean mIsStarted = false;

    private Location() {
        mLocationClient = new LocationClient(AppEx.ct());
        setLocationOption(1000);

        mListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mListener);
    }

    public static Location inst() {
        if (mInst == null) {
            mInst = new Location();
        }
        return mInst;
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (!mIsStarted) {
                // 如果结束了, 不进行通知
                return;
            }

            LogMgr.d(TAG, "onReceiveLocation1 = " + location.getCity());
            LogMgr.d(TAG, "onReceiveLocation2 = " + location.getLocType());
            if (location == null || location.getLocType() != 161) {
                LogMgr.d(TAG, "onReceiveLocation: location failed");
                LocationNotifier.inst().notify(false, null);
                return;
            }
            stop();

            Gps g = new Gps();
            g.put(TGps.longitude, location.getLongitude());
            g.put(TGps.latitude, location.getLatitude());
            g.put(TGps.address, location.getAddress());
            LocationNotifier.inst().notify(true, g);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    public void start() {
        if (!mIsStarted) {
            mIsStarted = true;
            mLocationClient.start();
        }
    }

    public void stop() {
        if (mIsStarted) {
            mIsStarted = false;
            mLocationClient.stop();
        }
    }

    private static final String KBaiduCoorType = "bd09ll";
    private static final String KProdName = "应用App";

    private void setLocationOption(int scanSpan) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);// 设置定位模式, 暂时使用网络模式,
        // GPS的话无法关闭通知栏图标
        option.setCoorType(KBaiduCoorType);// 返回的定位结果是百度经纬度,默认值gcj02
        option.setProdName(KProdName);
        option.setScanSpan(scanSpan);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(false);// 返回的定位结果包含手机机头的方向

        mLocationClient.setLocOption(option);
    }

    public void onDestroy() {
        stop();
        if (mListener != null) {
            mLocationClient.unRegisterLocationListener(mListener);
            mListener = null;
        }

        mInst = null;
    }
}

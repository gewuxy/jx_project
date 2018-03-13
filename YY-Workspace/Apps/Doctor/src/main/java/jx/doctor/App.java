package jx.doctor;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.squareup.leakcanary.LeakCanary;

import jx.doctor.Constants.PageConstants;
import jx.doctor.network.NetFactory;
import jx.doctor.network.NetworkApiDescriptor;
import jx.doctor.network.UrlUtil;
import jx.doctor.util.CacheUtil;
import lib.jg.JAnalyticsStats;
import lib.jg.JG;
import lib.jx.BaseApp;
import lib.network.NetworkConfig;
import lib.platform.Platform;
import lib.ys.YSLog;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.config.ListConfigBuilder;
import lib.ys.config.NavBarConfig;
import lib.ys.stats.Stats;

/**
 * @author yuansui
 * @since 2017/4/5
 */
public class App extends BaseApp {

    /**
     * nav bar定义
     */
    public interface NavBarVal {
        int KHeightDp = 44;
        int KIconSizeDp = 16;
        int KIconPaddingHorizontalDp = 12;
        int KTextMarginHorizontalDp = 12;
        int KLeftTextSizeDp = 14;
        int KMidTextSizeDp = 17;
        int KRightTextSizeDp = 14;
    }

    @Override
    protected AppConfig configureApp() {
        return AppConfig.newBuilder()
                .bgColorRes(R.color.app_bg)
                .enableFlatBar(false)
                .initRefreshWay(RefreshWay.embed)
                .enableSwipeFinish(BuildConfig.SWIPE_BACK_ENABLE)
                .build();
    }

    @Override
    protected NetworkConfig configureNetwork() {
        return NetworkConfig.newBuilder()
                .connectTimeout(KTimeout)
                .readTimeout(KTimeout)
                .writeTimeout(KTimeout)
                .headersMaker(() -> NetFactory.getBaseHeader())
                .timeoutToast(getString(R.string.connect_timeout))
                .disconnectToast(getString(R.string.network_disabled))
                .cacheDir(CacheUtil.getUploadCacheDir())
                .build();
    }

    @Override
    protected NavBarConfig configureNavBar() {
        return NavBarConfig.newBuilder()
                .heightDp(NavBarVal.KHeightDp)
                .bgColorRes(R.color.app_nav_bar_bg)
                .iconPaddingHorizontalDp(NavBarVal.KIconPaddingHorizontalDp)
                .iconSizeDp(NavBarVal.KIconSizeDp)
                .textColorRes(R.color.nav_bar_text_selector)
                .textMarginHorizontalDp(NavBarVal.KTextMarginHorizontalDp)
                .textSizeLeftDp(NavBarVal.KLeftTextSizeDp)
                .textSizeMidDp(NavBarVal.KMidTextSizeDp)
                .textSizeRightDp(NavBarVal.KRightTextSizeDp)
                .focusBgDrawableRes(R.drawable.nav_bar_selector)
                .build();
    }

    @Override
    protected ListConfig configureList() {
        return ListConfigBuilder.create()
                .type(PageDownType.page)
                .initOffset(PageConstants.KPage)
                .limit(PageConstants.KPageSize)
                .build();
    }

    @Override
    protected void init() {
        // log
        YSLog.setDebugState(BuildConfig.DEBUG_LOG);

        if (BuildConfig.DEBUG_NETWORK) {
            LeakCanary.install(this);
        }

        UrlUtil.setDebug(BuildConfig.DEBUG_NETWORK);
        NetworkApiDescriptor.setDebuggable(BuildConfig.DEBUG_NETWORK);

        //百度地图
        SDKInitializer.initialize(this);

        // fixme
        Platform.init(this, "21454499cef00", "da83f1b9d28a32e0d57004d58e1eb318");

        // 临时的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        JG.init(this, BuildConfig.DEBUG_LOG);
        Stats.init(new JAnalyticsStats(), BuildConfig.DEBUG_LOG);

    }

    @Override
    protected String getNetworkImageCacheDir() {
        return CacheUtil.getBmpCacheDir();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

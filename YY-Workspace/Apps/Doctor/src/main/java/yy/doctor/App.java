package yy.doctor;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;

import lib.jg.JAnalyticsStats;
import lib.jg.JG;
import lib.network.NetworkConfig;
import lib.ys.YSLog;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.config.ListConfigBuilder;
import lib.ys.config.NavBarConfig;
import lib.ys.stats.Stats;
import lib.yy.BaseApp;
import yy.doctor.Constants.PageConstants;
import yy.doctor.network.UrlUtil;
import yy.doctor.util.CacheUtil;

/**
 * @author yuansui
 * @since 2017/4/5
 */
public class App extends BaseApp {

    public static final int KTitleBarHeightDp = 44;
    private static final int KTitleBarIconSizeDp = 16;
    private static final int KTitleBarIconPaddingHorizontalDp = 12;
    private static final int KTitleBarTextMarginHorizontalDp = 12;
    public static final int KTitleBarLeftTextSizeDp = 14;
    public static final int KTitleBarMidTextSizeDp = 17;
    public static final int KTitleBarRightTextSizeDp = 14;

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
                .connectTimeout(15)
                .readTimeout(15)
                .writeTimeout(15)
                .timeoutToast(getString(R.string.connect_timeout))
                .cacheDir(CacheUtil.getUploadCacheDir())
                .build();
    }

    @Override
    protected NavBarConfig configureNavBar() {
        return NavBarConfig.newBuilder()
                .heightDp(KTitleBarHeightDp)
                .bgColorRes(R.color.app_nav_bar_bg)
                .iconPaddingHorizontalDp(KTitleBarIconPaddingHorizontalDp)
                .iconSizeDp(KTitleBarIconSizeDp)
                .textColorRes(R.color.nav_bar_text_selector)
                .textMarginHorizontalDp(KTitleBarTextMarginHorizontalDp)
                .textSizeLeftDp(KTitleBarLeftTextSizeDp)
                .textSizeMidDp(KTitleBarMidTextSizeDp)
                .textSizeRightDp(KTitleBarRightTextSizeDp)
                .focusBgDrawableRes(R.drawable.nav_bar_selector)
                .build();
    }

    @Override
    protected ListConfig configureList() {
        return ListConfigBuilder.create()
                .type(PageDownType.page)
                .initOffset(PageConstants.KPage)
                .limit(PageConstants.KPageSize)
//                .footerClz(ListFooter.class)
                .build();
    }

    @Override
    protected void init() {
        // log
        YSLog.setDebugState(BuildConfig.DEBUG_LOG);

        UrlUtil.setDebug(BuildConfig.DEBUG_NETWORK);

        //百度地图
        SDKInitializer.initialize(this);

        // 临时的
        if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        JG.init(this, BuildConfig.DEBUG_LOG);
        Stats.init(new JAnalyticsStats(), BuildConfig.DEBUG_LOG);

        if (BuildConfig.DEBUG_LOG) {
//            LeakCanary.install(this);
        }
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

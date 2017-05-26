package yy.doctor;

import android.os.Build;
import android.os.StrictMode;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jiguang.share.android.api.JShareInterface;
import cn.jpush.android.api.JPushInterface;
import lib.network.NetworkConfig;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.NavBarConfig;
import lib.ys.ui.other.NavBar;
import lib.yy.BaseApp;
import yy.doctor.network.UrlUtil;
import yy.doctor.util.CacheUtil;
import yy.doctor.view.ListFooter;

/**
 * @author yuansui
 * @since 2017/4/5
 */
public class App extends BaseApp {

    public static final int KTitleBarHeightDp = 44;
    private static final int KTitleBarIconSizeDp = 16;
    private static final int KTitleBarIconPaddingHorizontalDp = 12;
    private static final int KTitleBarTextMarginHorizontalDp = 12;
    private static final int KTitleBarTextSizeDp = 16;

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
                .connectTimeout(30000)
                .readTimeout(30000)
                .build();
    }

    @Override
    protected void init() {
        // log
        LogMgr.setDebugState(BuildConfig.DEBUG_LOG);

        UrlUtil.setDebug(BuildConfig.DEBUG_NETWORK);

        // 导航栏
        NavBarConfig navBarConfig = NavBarConfig.newBuilder()
                .heightDp(KTitleBarHeightDp)
                .bgColorRes(R.color.app_nav_bar_bg)
                .iconPaddingHorizontalDp(KTitleBarIconPaddingHorizontalDp)
                .iconSizeDp(KTitleBarIconSizeDp)
                .textColorRes(R.color.nav_bar_text_selector)
                .textMarginHorizontalDp(KTitleBarTextMarginHorizontalDp)
                .textSizeLeftDp(KTitleBarTextSizeDp)
                .textSizeMidDp(KTitleBarTextSizeDp)
                .textSizeRightDp(KTitleBarTextSizeDp)
                .focusBgDrawableRes(R.drawable.nav_bar_selector)
                .build();
        NavBar.initialize(navBarConfig);

        ListConfig.footerClz(ListFooter.class);

        // 临时的
        if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        JAnalyticsInterface.setDebugMode(BuildConfig.DEBUG_NETWORK);
        JAnalyticsInterface.init(this);
        JShareInterface.setDebugModel(BuildConfig.DEBUG_NETWORK);
        JShareInterface.init(this);
        JPushInterface.setDebugMode(BuildConfig.DEBUG_NETWORK);
        JPushInterface.init(this);

//        Stats.init("", BuildConfig.DEBUG_LOG);
    }

    @Override
    protected String getNetworkImageCacheDir() {
        return CacheUtil.getBmpCacheDir();
    }

}

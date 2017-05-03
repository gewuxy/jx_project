package yy.doctor;

import android.os.Build;
import android.os.StrictMode;

import lib.ys.LogMgr;
import lib.ys.config.AppConfig;
import lib.ys.config.NavBarConfig;
import lib.ys.ex.NavBar;
import lib.yy.BaseApp;
import yy.doctor.util.CacheUtil;

/**
 * @author yuansui
 * @since 2017/4/5
 */
public class App extends BaseApp {

    private static final int KTitleBarHeightDp = 44;
    private static final int KTitleBarIconSizeDp = 16;
    private static final int KTitleBarIconPaddingHorizontalDp = 12;
    private static final int KTitleBarTextMarginHorizontalDp = 12;
    private static final int KTitleBarTextSizeDp = 16;

    @Override
    protected AppConfig makeConfig() {
        return AppConfig.newBuilder()
                .bgColorRes(R.color.app_bg)
                .enableFlatBar(false)
                .enableSwipeFinish(BuildConfig.SWIPE_BACK_ENABLE)
                .build();
    }

    @Override
    protected void init() {
        // log
        LogMgr.setDebugState(BuildConfig.DEBUG_LOG);

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
                .focusBgColorRes(R.color.nav_bar_bg_focus)
                .build();
        NavBar.initialize(navBarConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    @Override
    protected String getNetworkImageCacheDir() {
        return CacheUtil.getBmpCacheDir();
    }

}

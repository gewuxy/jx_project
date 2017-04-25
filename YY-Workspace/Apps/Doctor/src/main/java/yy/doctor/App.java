package yy.doctor;

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
    private static final int KTitleBarTextSize = 16;

    @Override
    protected AppConfig makeConfig() {
        return AppConfig.newBuilder()
                .bgColorRes(R.color.app_bg)
                .enableFlatBar(true)
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
                .textSizeLeftDp(KTitleBarTextSize)
                .textSizeMidDp(KTitleBarTextSize)
                .textSizeRightDp(KTitleBarTextSize)
                .focusBgColorRes(R.color.title_click_bg_focus)
                .build();
        NavBar.initialize(navBarConfig);
    }

    @Override
    protected String getNetworkImageCacheDir() {
        return CacheUtil.getBmpCacheDir();
    }

}

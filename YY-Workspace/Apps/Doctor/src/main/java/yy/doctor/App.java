package yy.doctor;

import lib.ys.LogMgr;
import lib.ys.config.AppConfig;
import lib.ys.config.NavBarConfig;
import lib.yy.BaseApp;
import yy.doctor.util.CacheUtil;

/**
 * @author Administrator
 * @since 2017/4/5
 */
public class App extends BaseApp {

    private int KTitleBarHeightDp = 44;
    private int KTitleBarIconSizeDp = 16;
    private int KTitleBarIconPaddingHorizontalDp = 12;
    private int KTitleBarTextMarginHorizontalDp = 12;
    private int KTitleBarTextSize = 16;

    @Override
    protected void setParams() {

        NavBarConfig.inst().inst().heightDp(KTitleBarHeightDp);
        NavBarConfig.inst().bgRes(R.color.app_nav_bar_bg);
        NavBarConfig.inst().iconPaddingHorizontalDp(KTitleBarIconPaddingHorizontalDp);
        NavBarConfig.inst().iconSizeDp(KTitleBarIconSizeDp);
        NavBarConfig.inst().textColorRes(R.color.nav_bar_text_selector);
        NavBarConfig.inst().textMarginHorizontalDp(KTitleBarTextMarginHorizontalDp);
        NavBarConfig.inst().textSizeLeftDp(KTitleBarTextSize);
        NavBarConfig.inst().textSizeMidDp(KTitleBarTextSize);
        NavBarConfig.inst().textSizeRightDp(KTitleBarTextSize);
        NavBarConfig.inst().focusBgColorRes(R.color.title_click_bg_focus);

        AppConfig.appBgColorId(R.color.app_bg);
        AppConfig.enableSwipeFinish(BuildConfig.SWIPE_BACK_ENABLE);

        LogMgr.setDebugState(BuildConfig.DEBUG_LOG);
    }

    @Override
    protected String getNetworkImageCacheDir() {
        return CacheUtil.getBmpCacheDir();
    }

}

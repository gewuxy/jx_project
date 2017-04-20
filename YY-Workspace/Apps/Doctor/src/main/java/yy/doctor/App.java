package yy.doctor;

import lib.ys.config.AppConfig;
import lib.ys.config.TitleBarConfig;
import lib.ys.util.res.ResLoader;
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

        TitleBarConfig.heightDp(KTitleBarHeightDp);
        TitleBarConfig.bgColor(ResLoader.getColor(R.color.app_title_bar_bg));
        TitleBarConfig.iconPaddingHorizontalDp(KTitleBarIconPaddingHorizontalDp);
        TitleBarConfig.iconSizeDp(KTitleBarIconSizeDp);

        TitleBarConfig.textColor(ResLoader.getColor(R.color.white));
        TitleBarConfig.textMarginHorizontalDp(KTitleBarTextMarginHorizontalDp);
        TitleBarConfig.textSizeLeftDp(KTitleBarTextSize);
        TitleBarConfig.textSizeMidDp(KTitleBarTextSize);
        TitleBarConfig.textSizeRightDp(KTitleBarTextSize);

        AppConfig.appBgColorId(R.color.app_bg);
        AppConfig.enableSwipeFinish(false);

    }

    @Override
    protected String getNetworkImageCacheDir() {
        return CacheUtil.getBmpCacheDir();
    }

}

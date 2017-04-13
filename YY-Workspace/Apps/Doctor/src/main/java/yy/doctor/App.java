package yy.doctor;

import android.graphics.Color;

import lib.ys.config.AppConfig;
import lib.ys.config.TitleBarConfig;
import lib.yy.BaseApp;
import yy.doctor.util.CacheUtil;

/**
 * @author Administrator
 * @since 2017/4/5
 */
public class App extends BaseApp {

    @Override
    protected void setParams() {

        TitleBarConfig.bgColor(Color.BLUE);
        TitleBarConfig.heightDp(44);

        AppConfig.appBgColorId(R.color.app_bg);
        AppConfig.enableSwipeFinish(false);

    }

    @Override
    protected String getNetworkImageCacheDir() {
        return CacheUtil.getBmpCacheDir();
    }
}

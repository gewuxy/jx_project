package yy.doctor;

import android.graphics.Color;

import lib.ys.config.AppConfig;
import lib.ys.config.TitleBarConfig;
import lib.yy.BaseApp;

/**
 * @author Administrator
 * @since 2017/4/5
 */
public class App extends BaseApp {

    @Override
    protected void setParams() {
        TitleBarConfig.bgColor(Color.WHITE);
        TitleBarConfig.heightDp(44);

        AppConfig.enableSwipeFinish(false);


    }

    @Override
    protected String getNetworkImageCacheDir() {
        return null;
    }
}

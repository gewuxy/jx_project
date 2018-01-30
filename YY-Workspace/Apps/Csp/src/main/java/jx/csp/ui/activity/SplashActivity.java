package jx.csp.ui.activity;

import android.widget.ImageView;

import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.constant.LangType;
import jx.csp.sp.SpApp;
import lib.ys.ui.activity.SplashActivityEx;

/**
 * 启动页
 *
 * @auther yuansui
 * @since 2017/9/20
 */

public class SplashActivity extends SplashActivityEx {

    @Override
    public int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    public void findViews() {
    }

    @Override
    protected long getPastDelay() {
        return 1000;
    }

    @Override
    protected void goPast() {
        if (BuildConfig.TEST) {
            startActivity(TestActivity.class);
        } else {
            startActivity(AdActivity.class);
        }
    }
}

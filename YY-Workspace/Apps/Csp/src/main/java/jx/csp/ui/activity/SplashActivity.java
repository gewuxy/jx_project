package jx.csp.ui.activity;

import jx.csp.BuildConfig;
import lib.ys.ui.activity.SplashActivityEx;
import jx.csp.R;

/**
 * 启动页
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
            startActivity(AdvActivity.class);
        }
    }
}

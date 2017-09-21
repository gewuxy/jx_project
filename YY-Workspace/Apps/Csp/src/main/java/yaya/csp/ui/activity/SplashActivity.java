package yaya.csp.ui.activity;

import lib.ys.ui.activity.SplashActivityEx;
import yaya.csp.R;

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
        startActivity(AdvActivity.class);
    }
}

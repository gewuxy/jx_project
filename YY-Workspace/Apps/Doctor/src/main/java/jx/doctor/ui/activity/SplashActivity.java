package jx.doctor.ui.activity;

import android.os.Bundle;

import lib.ys.ui.activity.SimpleSplashActivityEx;
import jx.doctor.BuildConfig;
import jx.doctor.R;
import jx.doctor.serv.GlConfigServRouter;

/**
 * 推广页面
 *
 * @author CaiXiang
 * @since 2017/4/5
 */
public class SplashActivity extends SimpleSplashActivityEx {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlConfigServRouter.create().route(this);
    }

    @Override
    protected int getSplashImageResId() {
        return R.drawable.splash_bg;
    }

    @Override
    protected long getPastDelay() {
        return 1000;
    }

    @Override
    protected void startOutAnim() {
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

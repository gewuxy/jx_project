package yy.doctor.ui.activity;

import android.os.Bundle;

import lib.ys.ui.activity.SimpleSplashActivityEx;
import yy.doctor.R;
import yy.doctor.serv.GlConfigServRouter;

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
    protected void goPast() {
        startActivity(AdActivity.class);
    }
}

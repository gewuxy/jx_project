package jx.doctor.ui.activity;

import android.os.Bundle;

import jx.doctor.BuildConfig;
import jx.doctor.serv.GlConfigServRouter;
import lib.ys.ui.activity.SimpleSplashActivityEx;
import lib.ys.util.PackageUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.res.ResUtil.ResDefType;

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
        return ResLoader.getIdentifier(PackageUtil.getMetaValue("SPLASH_BG"), ResDefType.drawable);
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

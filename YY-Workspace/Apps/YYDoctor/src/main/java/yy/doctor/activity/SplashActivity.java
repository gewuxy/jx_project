package yy.doctor.activity;

import android.support.annotation.NonNull;

import lib.ys.activity.SplashActivityEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class SplashActivity extends SplashActivityEx {

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void goPast() {
        startActivity(MainActivity.class);
    }
}

package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import lib.ys.activity.SplashActivityEx;
import yy.doctor.BuildConfig;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class SplashActivity extends SplashActivityEx {

    private ImageView mIv;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    public void findViews() {
        mIv = findView(R.id.splash_iv);
    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        mIv.setImageResource(R.mipmap.splash_bg);
    }

    @Override
    protected void goPast() {
        if (BuildConfig.TEST) {
            startActivity(TestActivity.class);
        } else {
            startActivity(LoginActivity.class);
        }
    }
}

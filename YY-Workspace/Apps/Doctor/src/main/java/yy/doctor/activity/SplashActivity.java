package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import lib.ys.ui.activity.SplashActivityEx;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.model.Profile;

/**
 * 推广页面
 *
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
    public void setViews() {
        super.setViews();

        mIv.setImageResource(R.mipmap.splash_bg);
    }

    @Override
    protected long getPastDelay() {
        return 1000;
    }

    @Override
    protected void goPast() {
        //登录过了并且没有退出过的话直接跳转到首页
        if (BuildConfig.TEST) {
            startActivity(TestActivity.class);
        } else if (Profile.inst().isLogin()) {
            startActivity(MainActivity.class);
        } else {
            startActivity(LoginActivity.class);
        }
    }

}

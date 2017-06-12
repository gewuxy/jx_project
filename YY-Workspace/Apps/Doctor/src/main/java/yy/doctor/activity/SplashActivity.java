package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import lib.ys.ui.activity.SplashActivityEx;
import lib.ys.util.permission.Permission;
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

    private static final int KPermissionCodeLocation = 10;
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

        //检查有没有定位权限   没有的话直接弹dialog
        if (checkPermission(KPermissionCodeLocation, Permission.location)) {
        }

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

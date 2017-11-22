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

    private ImageView mSplashBg;
    private ImageView mSplashBgEn;

    @Override
    public int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    public void findViews() {
        mSplashBg = findView(R.id.start_bg);
        mSplashBgEn = findView(R.id.start_bg_en);
    }

    @Override
    public void setViews() {
        super.setViews();
       if (SpApp.inst().getLangType()== LangType.en) {
           showView(mSplashBgEn);
           goneView(mSplashBg);
       }
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

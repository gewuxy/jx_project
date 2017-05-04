package lib.ys.activity;

import android.support.annotation.CallSuper;
import android.widget.ImageView;

import lib.ys.R;


abstract public class SimpleSplashActivityEx extends SplashActivityEx {

    private ImageView mIv;

    @Override
    public int getContentViewId() {
        return R.layout.activity_splash_ex;
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mIv = findView(R.id.splash_iv);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mIv.setImageResource(getSplashImageResId());
    }

    abstract protected int getSplashImageResId();
}

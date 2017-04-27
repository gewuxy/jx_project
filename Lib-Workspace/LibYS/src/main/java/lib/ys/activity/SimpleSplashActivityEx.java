package lib.ys.activity;

import android.widget.ImageView;

import lib.ys.R;


abstract public class SimpleSplashActivityEx extends SplashActivityEx {

    private ImageView mIv;

    @Override
    public int getContentViewId() {
        return R.layout.activity_splash_ex;
    }

    @Override
    public void findViews() {
        super.findViews();

        mIv = (ImageView) findViewById(R.id.splash_iv);
    }

    @Override
    public void setViews() {
        super.setViews();

        mIv.setImageResource(getSplashImageResId());
    }

    abstract protected int getSplashImageResId();
}

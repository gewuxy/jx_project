package lib.ys.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewTreeObserver.OnPreDrawListener;

import lib.ys.dialog.DialogEx;
import lib.ys.ex.NavBar;

abstract public class SplashActivityEx extends ActivityEx {

    protected Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                goPast();
                finish();
            }
        };
    }

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        if (getDecorView().getViewTreeObserver().isAlive()) {
            addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    // 不在这里处理的话. 总是会有黑屏然后直接进入主界面的情况出现
                    mHandler.sendEmptyMessageDelayed(0, getPastDelay());
                    getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        } else {
            mHandler.sendEmptyMessageDelayed(0, getPastDelay());
        }
    }

    /**
     * splash显示完以后开启下一个跳转的activity, 然后会自动finish此activity
     */
    abstract protected void goPast();

    @Override
    protected DialogEx initLoadingDialog() {
        // splash不需要加载
        return null;
    }

    @Override
    protected void startInAnim() {
    }

    @Override
    protected void startOutAnim() {
    }

    /**
     * @return 毫秒
     */
    protected long getPastDelay() {
        return 800;
    }
}

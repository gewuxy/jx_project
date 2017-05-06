package lib.ys.activity;

import android.view.ViewTreeObserver.OnPreDrawListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import lib.ys.dialog.DialogEx;
import lib.ys.ex.NavBar;

abstract public class SplashActivityEx extends ActivityEx {

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

        boolean b = addOnPreDrawListener(new OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                pass();
                removeOnPreDrawListener(this);
                return false;
            }
        });

        if (!b) {
            pass();
        }
    }

    private void pass() {
        Observable.just((Runnable) () -> {
            goPast();
            finish();
        }).delay(getPastDelay(), TimeUnit.MILLISECONDS).subscribe(Runnable::run);
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

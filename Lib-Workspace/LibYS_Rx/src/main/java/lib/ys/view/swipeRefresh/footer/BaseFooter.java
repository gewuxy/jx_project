package lib.ys.view.swipeRefresh.footer;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import lib.ys.fitter.LayoutFitter;
import lib.ys.ui.interfaces.listener.OnRetryClickListener;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.swipeRefresh.interfaces.Extend;

/**
 * @author yuansui
 */
abstract public class BaseFooter extends LinearLayout implements Extend {

    private View mContentView;
    private OnRetryClickListener mOnRetryClickLsn;

    @ExtendState
    private int mState = ExtendState.normal;

    public BaseFooter(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContentView = LayoutInflater.from(context).inflate(getContentViewId(), null);
        addView(mContentView, LayoutUtil.getLinearParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LayoutFitter.fit(mContentView);

        findViews();
        setViews();
    }

    @LayoutRes
    abstract protected int getContentViewId();

    abstract protected void findViews();

    abstract protected void setViews();

    protected void setOnRetryClickView(View v) {
        v.setOnClickListener(v1 -> {
            if (mOnRetryClickLsn != null) {
                boolean result = mOnRetryClickLsn.onRetryClick();
                if (result) {
                    changeState(ExtendState.loading);
                }
            }
        });
    }

    public void setOnRetryLoadClickListener(OnRetryClickListener lisn) {
        mOnRetryClickLsn = lisn;
    }

    public void hide() {
        goneView(mContentView);
    }

    public void show() {
        showView(mContentView);
    }

    @Override
    public final void changeState(@ExtendState int state) {
        if (mState == state) {
            return;
        }

        onStateChanged(state);
        mState = state;
    }

    private void onStateChanged(@ExtendState int state) {
        switch (state) {
            case ExtendState.normal:
                onNormal();
                break;
            case ExtendState.ready:
                onReady();
                break;
            case ExtendState.loading:
                onLoading();
                break;
            case ExtendState.failed:
                onFailed();
                break;
            case ExtendState.finish:
                onFinish();
                break;
        }
    }

    protected void showView(View v) {
        ViewUtil.showView(v);
    }

    protected void hideView(View v) {
        ViewUtil.hideView(v);
    }

    protected void goneView(View v) {
        ViewUtil.goneView(v);
    }

}

package lib.ys.view.swipeRefresh.footer;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import lib.ys.fitter.LayoutFitter;
import lib.ys.interfaces.OnRetryClickListener;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.swipeRefresh.interfaces.IExtend;

/**
 * @author yuansui
 */
abstract public class BaseFooter extends LinearLayout implements IExtend {

    private View mContentView;
    private OnRetryClickListener mOnRetryClickLsn;

    private TState mState = TState.normal;

    public BaseFooter(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContentView = LayoutInflater.from(context).inflate(getContentViewId(), null);
        addView(mContentView, LayoutUtil.getLinearParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LayoutFitter.fit(mContentView);

        findViews();
        setViewsValue();
    }

    @LayoutRes
    abstract int getContentViewId();

    abstract void findViews();

    abstract void setViewsValue();

    protected void setOnRetryClickView(View v) {
        v.setOnClickListener(v1 -> {
            if (mOnRetryClickLsn != null) {
                boolean result = mOnRetryClickLsn.onRetryClick();
                if (result) {
                    changeState(TState.loading);
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
    public final void changeState(TState state) {
        if (mState == state) {
            return;
        }

        onStateChanged(state);
        mState = state;
    }

    private void onStateChanged(TState state) {
        switch (state) {
            case normal:
                onNormal();
                break;
            case ready:
                onReady();
                break;
            case loading:
                onLoading();
                break;
            case failed:
                onFailed();
                break;
            case finish:
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

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
import lib.ys.view.swipeRefresh.interfaces.IExtend;

/**
 * @author yuansui
 */
abstract public class BaseFooter extends LinearLayout implements IExtend {

    private View mContentView;
    private OnRetryClickListener mOnRetryClickLsn;

    @IExtendStatus
    private int mState = IExtendStatus.normal;

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
                    changeStatus(IExtendStatus.loading);
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
    public final void changeStatus(@IExtendStatus int status) {
        if (mState == status) {
            return;
        }

        onStateChanged(status);
        mState = status;
    }

    private void onStateChanged(@IExtendStatus int state) {
        switch (state) {
            case IExtendStatus.normal:
                onNormal();
                break;
            case IExtendStatus.ready:
                onReady();
                break;
            case IExtendStatus.loading:
                onLoading();
                break;
            case IExtendStatus.failed:
                onFailed();
                break;
            case IExtendStatus.finish:
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

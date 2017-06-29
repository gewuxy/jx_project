package lib.ys.view.swipeRefresh.header;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import lib.ys.util.UIUtil;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.swipeRefresh.base.BaseSRLayout;
import lib.ys.view.swipeRefresh.interfaces.IExtend;

/**
 * @author yuansui
 */
abstract public class BaseHeader extends LinearLayout implements IExtend {

    private BaseSRLayout mSRLayout;
    private float mPercent;
    private View mContentView;

    @ExtendState
    private int mState = ExtendState.normal;

    public BaseHeader(Context context, BaseSRLayout layout) {
        super(context);

        mSRLayout = layout;

        mContentView = initContentView();
        int h = UIUtil.dpToPx(BaseSRLayout.KDragMaxDistanceDp * 1.3f);
        addView(mContentView, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, h));
    }

    protected BaseSRLayout getSRLayout() {
        return mSRLayout;
    }

    protected View getContentView() {
        return mContentView;
    }

    abstract protected View initContentView();

    public void setPercent(float percent, boolean invalidate) {
        mPercent = percent;
        onPercentChanged(percent, invalidate);
    }

    protected void onPercentChanged(float percent, boolean invalidate) {
    }

    protected float getPercent() {
        return mPercent;
    }

    public abstract void offset(int offset);

    protected void showView(View v) {
        ViewUtil.showView(v);
    }

    protected void hideView(View v) {
        ViewUtil.hideView(v);
    }

    protected void goneView(View v) {
        ViewUtil.goneView(v);
    }

    @Override
    public final void changeState(@ExtendState int state) {
        if (mState == state) {
            return;
        }

        onStateChanged(state);
        mState = state;
    }

    @ExtendState
    protected int getLastState() {
        return mState;
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
}

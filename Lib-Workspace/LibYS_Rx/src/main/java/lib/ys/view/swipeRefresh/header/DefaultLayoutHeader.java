package lib.ys.view.swipeRefresh.header;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import lib.ys.R;
import lib.ys.view.ProgressView;
import lib.ys.view.swipeRefresh.base.BaseSRLayout;

/**
 * @author yuansui
 */
public class DefaultLayoutHeader extends BaseLayoutHeader {

    private View mIvArrow;
    private View mLayoutRefresh;
    private TextView mTvHint;
    private View mLayoutLoading;
    private ProgressView mProgressView;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    public DefaultLayoutHeader(Context context, BaseSRLayout layout) {
        super(context, layout);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.sr_list_header;
    }

    @Override
    protected void findViews() {
        mLayoutRefresh = findViewById(R.id.sr_list_header_layout_refresh);
        mTvHint = (TextView) findViewById(R.id.sr_list_header_tv_hint);
        mIvArrow = findViewById(R.id.sr_list_header_iv_arrow);
        mLayoutLoading = findViewById(R.id.sr_list_header_layout_loading);
        mProgressView = (ProgressView) findViewById(R.id.sr_list_header_progress_view);
    }

    @Override
    protected void setViewsValue() {
    }

    protected Animation getAnimRotateUp() {
        if (mRotateUpAnim == null) {
            mRotateUpAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_up_opposite);
        }
        return mRotateUpAnim;
    }

    protected Animation getAnimRotateDown() {
        if (mRotateDownAnim == null) {
            mRotateDownAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_down_positive);
        }
        return mRotateDownAnim;
    }

    @Override
    public void onNormal() {
        showView(mLayoutRefresh);
        goneView(mLayoutLoading);
        mProgressView.stop();

        if (getLastState() == IExtendStatus.ready) {
            mIvArrow.startAnimation(getAnimRotateDown());
        } else if (getLastState() == IExtendStatus.loading) {
            mIvArrow.clearAnimation();
        }

        mTvHint.setText(R.string.sr_list_header_hint_normal);
    }

    @Override
    public void onReady() {
        showView(mLayoutRefresh);
        goneView(mLayoutLoading);

        if (getLastState() != IExtendStatus.ready) {
            mIvArrow.clearAnimation();
            mIvArrow.startAnimation(getAnimRotateUp());
            mTvHint.setText(R.string.sr_list_header_hint_ready);
        }
    }

    @Override
    public void onLoading() {
        mIvArrow.clearAnimation();
        goneView(mLayoutRefresh);
        showView(mLayoutLoading);
        mProgressView.start();
    }

    @Override
    public void onFailed() {
    }

    @Override
    public void onFinish() {
        goneView(mLayoutRefresh);
        showView(mLayoutLoading);
    }
}

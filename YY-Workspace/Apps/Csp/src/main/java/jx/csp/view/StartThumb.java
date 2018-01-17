package jx.csp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import jx.csp.R;
import lib.ys.fitter.Fitter;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.shape.CircleRenderer;
import lib.ys.util.AnimateUtil;
import lib.ys.util.view.LayoutUtil;

/**
 * @auther : GuoXuan
 * @since : 2018/1/16
 */
public class StartThumb extends RelativeLayout {

    public interface OnScrollListener {
        void progress(int x);
    }

    public interface OnStartListener {
        void onClick();
    }

    private final float KHalf = 2;
//    private final long KDuration = 300L;

    private float mRadius;
    private int mSize;

    private float mLocation;
    private NetworkImageView mImageView;

    private OnScrollListener mListener;
    private OnStartListener mStartListener;

    private boolean mScroll;

    public StartThumb(Context context) {
        this(context, null);
    }

    public StartThumb(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StartThumb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mSize = Fitter.dimen(R.dimen.start_thumb);
        mRadius = mSize / KHalf;
        mLocation = mRadius;
        mScroll = true;

        mImageView = new NetworkImageView(getContext());
        addView(mImageView, mSize, LayoutUtil.MATCH_PARENT);
    }

    public void setListener(OnScrollListener listener) {
        mListener = listener;
    }

    public void setStartListener(OnStartListener startListener) {
        mStartListener = startListener;
    }

    /**
     * 设置滑块
     *
     * @param resId 滑块的id
     */
    public void setThumb(@DrawableRes int resId) {
        mImageView.res(resId)
                .renderer(new CircleRenderer())
                .load();
    }

    public void setLocation(float location) {
        if (location <= 0) {
            mLocation = 0;
        } else if (location > getMeasuredWidth() - mSize) {
            mLocation = getMeasuredWidth() - mSize;
        } else {
            mLocation = location;
        }
        AnimateUtil.translate(mImageView, mLocation, (getMeasuredHeight() - mSize) / KHalf, 0);
        if (mListener != null) {
            mListener.progress((int) ((mLocation + mRadius) / getMeasuredWidth() * 100));
        }
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                float x = event.getX();
                mScroll = x < mSize;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (!mScroll) {
                    return true;
                }
                float x = event.getX();
                setLocation(x);
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (!mScroll) {
                    return true;
                }
                float x = event.getX();
                if (x > getMeasuredWidth() / KHalf) {
                    x = getMeasuredWidth();
                } else {
                    x = 0;
                }
                setLocation(x);
                if (x == getMeasuredWidth() && mStartListener != null) {
                    mStartListener.onClick();
                }
            }
            break;
        }
        return true;
    }
}

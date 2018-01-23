package jx.csp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
public class StarThumb extends RelativeLayout {

    public interface OnScrollListener {
        void progress(int x);
    }

    public interface OnStartListener {
        void onClick();
    }

    private final float KHalf = 2;
    private final int KDuration = 1;

    private float mRadius;
    private int mSize;

    private NetworkImageView mImageView;

    private OnScrollListener mListener;
    private OnStartListener mStartListener;

    private boolean mScroll;

    private int mTarget; // 目标位置
    private float mLocation; // 抬手位置
    private float mOne; // 一次移动距离

    private Handler mHandler;

    public StarThumb(Context context) {
        this(context, null);
    }

    public StarThumb(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarThumb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mSize = Fitter.dimen(R.dimen.start_thumb);
        mRadius = mSize / KHalf;
        mScroll = true;
        mHandler = new Handler(msg -> {
            int count = (int) msg.obj;
            float location;
            if (mLocation < mTarget) {
                // 右移
                location = mLocation + count * mOne;
            } else {
                // 左移
                location = mLocation - count * mOne;
            }
            if (location > 0 && location < getMeasuredWidth()) {
                // 没到头
                Message m = Message.obtain();
                m.obj = count + 1;
                mHandler.sendMessageDelayed(m, KDuration);
            }
            setLocation(location);

            return true;
        });

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
        float fixX;
        if (location <= 0) {
            fixX = 0;
        } else if (location > getMeasuredWidth() - mSize) {
            fixX = getMeasuredWidth() - mSize;
            if (mStartListener != null) {
                mStartListener.onClick();
            }
        } else {
            fixX = location;
        }
        AnimateUtil.translate(mImageView, fixX, (getMeasuredHeight() - mSize) / KHalf, 0);
        if (mListener != null) {
            mListener.progress((int) ((fixX + mRadius) / getMeasuredWidth() * 100));
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mOne = (int) (getMeasuredWidth() / 100.0f);
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
                mLocation = x;
                if (x > getMeasuredWidth() / KHalf) {
                    x = getMeasuredWidth();
                } else {
                    x = 0;
                }
                mTarget = (int) x;

                Message m = Message.obtain();
                m.obj = 1;
                mHandler.sendMessageDelayed(m, KDuration);
            }
            break;
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

}

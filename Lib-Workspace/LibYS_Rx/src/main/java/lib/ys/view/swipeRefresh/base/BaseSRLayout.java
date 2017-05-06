package lib.ys.view.swipeRefresh.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import lib.ys.config.ListConfig;
import lib.ys.fitter.DpFitter;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.view.LayoutUtil;
import lib.ys.view.scrollableLayout.ScrollableLayout;
import lib.ys.view.swipeRefresh.header.BaseHeader;
import lib.ys.view.swipeRefresh.header.DefaultLayoutHeader;
import lib.ys.view.swipeRefresh.interfaces.IExtend.TState;
import lib.ys.view.swipeRefresh.interfaces.ISRCtrl;
import lib.ys.view.swipeRefresh.interfaces.ISRListener;

/**
 * 下拉刷新的外部layout, 根据网上代码更改
 */
abstract public class BaseSRLayout extends ViewGroup implements ISRCtrl {

    private static final int KMaxDragRate = 1;
    public static final int KDragMaxDistanceDp = 60;
    private static final float KDragRate = .5f;
    private static final float KFactor = 2f;
    public static final int KMaxAnimDuration = 700;
    private static final int KInvalidPointer = -1;

    private View mContentView;

    private Interpolator mDecelerateInterpolator;
    private int mTouchSlop;
    private int mTotalDragDistance;

    private BaseHeader mHeader;

    private float mCurrentDragPercent;
    private int mCurrentOffsetTop;

    private boolean mRefreshing;

    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialMotionY;
    private int mFrom;
    private float mFromDragPercent;
    private boolean mNotify;

    protected ISRListener mListener;
    private boolean mEnable = true;
    private boolean mIsAnimating = false;

    public BaseSRLayout(Context context) {
        this(context, null);
    }

    public BaseSRLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }

        mDecelerateInterpolator = new DecelerateInterpolator(KFactor);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTotalDragDistance = DpFitter.dp(KDragMaxDistanceDp);

        mHeader = ReflectionUtil.newInst(ListConfig.getHeaderClz(), getContext(), this);
        if (mHeader == null) {
            mHeader = new DefaultLayoutHeader(getContext(), this);
        }
        addView(mHeader, LayoutUtil.getViewGroupParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));

        mContentView = initContentView(context, attrs);
        if (mContentView == null) {
            throw new NullPointerException();
        }
        addView(mContentView, LayoutUtil.getViewGroupParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT));

        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }

    protected BaseHeader getHeader() {
        return mHeader;
    }

    abstract protected View initContentView(Context context, AttributeSet attrs);

    /**
     * This method sets padding for the refresh (progress) view.
     */
    public void setRefreshViewPadding(int left, int top, int right, int bottom) {
        mHeader.setPadding(left, top, right, bottom);
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mContentView == null) {
            return;
        }

        mHeader.measure(widthMeasureSpec, heightMeasureSpec);
        mContentView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnable || !isEnabled() || canChildScrollUp() || mRefreshing || mIsAnimating) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                setTargetOffsetTop(0, true);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == KInvalidPointer) {
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                mActivePointerId = KInvalidPointer;
            }
            break;
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
            }
            break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {

        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = y - mInitialMotionY;
                final float scrollTop = yDiff * KDragRate;
                mCurrentDragPercent = scrollTop / mTotalDragDistance;
                if (mCurrentDragPercent < 0) {
                    return false;
                }
                float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
                float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
                float slingshotDist = mTotalDragDistance;
                float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
                float extraMove = (slingshotDist) * tensionPercent / 2;
                int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);

                mHeader.setPercent(mCurrentDragPercent, true);
                if (mCurrentDragPercent >= KMaxDragRate) {
                    mHeader.changeState(TState.ready);
                } else {
                    mHeader.changeState(TState.normal);
                }

                setTargetOffsetTop(targetY - mCurrentOffsetTop, true);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == KInvalidPointer) {
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overScrollTop = (y - mInitialMotionY) * KDragRate;
                mIsBeingDragged = false;
                if (overScrollTop > mTotalDragDistance) {
                    setRefreshing(true, true);
                } else {
                    mRefreshing = false;
                    animateOffsetToStartPosition();
                }
                mActivePointerId = KInvalidPointer;
                return false;
            }
        }

        return true;
    }

    private void animateOffsetToStartPosition() {
        mIsAnimating = true;

        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;
        long animationDuration = Math.abs((long) (KMaxAnimDuration * mFromDragPercent));

        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(animationDuration);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mToStartListener);
        mHeader.clearAnimation();
        mHeader.startAnimation(mAnimateToStartPosition);
    }

    private void animateOffsetToCorrectPosition() {
        mIsAnimating = true;

        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(KMaxAnimDuration);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mHeader.clearAnimation();
        mHeader.startAnimation(mAnimateToCorrectPosition);

        if (mRefreshing) {
            mHeader.changeState(TState.loading);
            if (mNotify) {
                if (mListener != null) {
                    mListener.onSwipeRefresh();
                }
            }
        } else {
            mHeader.changeState(TState.normal);
            animateOffsetToStartPosition();
        }
        mCurrentOffsetTop = mContentView.getTop();
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget = mTotalDragDistance;
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mContentView.getTop();

            mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime;
            mHeader.setPercent(mCurrentDragPercent, false);

            setTargetOffsetTop(offset, false /* requires update */);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
        int offset = targetTop - mContentView.getTop();

        mCurrentDragPercent = targetPercent;
        mHeader.setPercent(mCurrentDragPercent, true);
        setTargetOffsetTop(offset, false);
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition();
            } else {
                animateOffsetToStartPosition();
                if (notify) {
                    if (mListener != null) {
                        mListener.onSwipeRefreshFinish();
                    }
                }
            }
        }
    }

    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mHeader.changeState(TState.normal);
            mCurrentOffsetTop = mContentView.getTop();
            mIsAnimating = false;
        }
    };

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
        mContentView.offsetTopAndBottom(offset);
        mHeader.offset(offset);
        mCurrentOffsetTop = mContentView.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private boolean canChildScrollUp() {
        if (mContentView instanceof ScrollableLayout) {
            return mContentView.getScrollY() > 0;
        } else {
            return ViewCompat.canScrollVertically(mContentView, -1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mContentView == null) {
            return;
        }

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mHeader.layout(left, top, left + width - right, top + height - bottom);
        mContentView.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);
    }

    public void setRefreshEnable(boolean enable) {
        mEnable = enable;
    }

    @Override
    public void setSRListener(ISRListener listener) {
        mListener = listener;
    }

    @Override
    public void startRefresh() {
        setRefreshing(true, true);
    }

    @Override
    public void stopRefresh() {
        setRefreshing(false, true);
    }

    @Override
    public boolean isSwipeRefreshing() {
        return mRefreshing;
    }

    protected View getContentView() {
        return mContentView;
    }
}


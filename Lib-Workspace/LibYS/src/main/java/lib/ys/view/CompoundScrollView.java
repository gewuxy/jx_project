package lib.ys.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * 复合scrollView, 适应类型: 头部为固定高度的view, 中部为固定高度的titleView, 底部为可拖动list
 */
abstract public class CompoundScrollView extends LinearLayout {

    protected String TAG = getClass().getSimpleName();

    public enum TCompoundScrollType {
        up, // 向上
        down; // 向下
    }

    private TCompoundScrollType mType;

    private int mTopHeight;
    private int mSlop;
    private float mDownY;
    private ICompoundScrollListener mListener;
    /**
     * 本次从down开始到本次up结束，其中是否被ICompoundScrollViewListener截断过事件
     */
    private boolean mIsIntercept = false;

    public enum TCompoundTopState {
        show, // 显示
        hide, // 隐藏
    }

    public interface ICompoundScrollListener {
        /**
         * scrollview是否拦截touch事件不再往子view传递事件
         */
        boolean isInterceptTouch();

        void onAnimationStart();

        void onAnimationEnd(TCompoundTopState state);

        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private View mTopView;
    private View mMidView;
    private View mBottomView;

    private boolean mRequestLayout = false;
    private boolean mIsAnimating = false;

    public CompoundScrollView(Context context) {
        super(context);
        init();
    }

    public CompoundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompoundScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);

        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mSlop = vc.getScaledTouchSlop();

        if (getViewTreeObserver().isAlive()) {
            getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mTopView = findTopView();
                    mMidView = findMidView();
                    mBottomView = findBottomView();

                    if (mTopView != null) {
                        mTopHeight = mTopView.getHeight() - getTopScrollKeepHeight();
                    }

                    int midHeight = 0;
                    if (mMidView != null) {
                        midHeight = mMidView.getHeight();
                    }

                    if (!mRequestLayout) {
                        int bottomHeight = getHeight() - midHeight - getTopScrollKeepHeight();
                        mBottomView.getLayoutParams().height = bottomHeight;
                        requestLayout();
                        mRequestLayout = true;
                    } else {
                        mRequestLayout = false;
                    }

                    // 不能去掉监听
                    // getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsAnimating) {
            // 动画过程中不响应
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mDownY = ev.getY();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                final float diffY = ev.getY() - mDownY;
                if (diffY > mSlop || diffY < -mSlop) {
                    final float curScrollY = getScrollY();
                    if (diffY > 0) {
                        // 向下拉
                        if (mListener != null && !mListener.isInterceptTouch()) {
                            mIsIntercept = true;
                            return super.onInterceptTouchEvent(ev);
                        }
                        if (curScrollY == mTopHeight) {
                            mType = TCompoundScrollType.down;
                            return true;
                        }
                    } else {
                        // 向上拉
                        if (mListener != null && !mListener.isInterceptTouch()) {
                            mIsIntercept = true;
                            return super.onInterceptTouchEvent(ev);
                        }
                        if (curScrollY == 0) {
                            mType = TCompoundScrollType.up;
                            return true;
                        }
                    }
                }
            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                mType = null;
                mIsIntercept = false;
            }
            break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsAnimating) {
            // 动画过程中不响应
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mDownY = ev.getY();
            }
            return true;
            case MotionEvent.ACTION_MOVE: {
                if (mIsIntercept) {
                    mDownY = ev.getY();
                    mIsIntercept = false;
                }

                final float curScrollY = getScrollY();
                if ((curScrollY <= 0 && mType == TCompoundScrollType.down) || (curScrollY >= mTopHeight && mType == TCompoundScrollType.up)) {
                    return super.onTouchEvent(ev);
                }

                if (curScrollY >= 0 && curScrollY <= mTopHeight) {
                    // 还能看见top view
                    float diffAbsY = Math.abs(ev.getY() - mDownY);

                    if (mType == null) {
                        float diffY = ev.getY() - mDownY;
                        if (diffY > 0 && curScrollY == mTopHeight) {
                            // 向下拉
                            mType = TCompoundScrollType.down;
                        } else if (diffY < 0 && curScrollY == 0) {
                            // 向上拉
                            mType = TCompoundScrollType.up;
                        } else {
                            // 不响应
                            mDownY = ev.getY();
                            break;
                        }
                    }

                    switch (mType) {
                        case up: {
                            if (diffAbsY > mTopHeight) {
                                diffAbsY = mTopHeight;
                            }
                            setScrollY((int) diffAbsY);
                        }
                        break;
                        case down: {
                            float offset = mTopHeight - diffAbsY;
                            if (offset < 0) {
                                offset = 0;
                            }
                            setScrollY((int) offset);
                        }
                        break;
                    }

                    return true;
                }
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (mType == null) {
                    break;
                }

                final float curY = getScrollY();
                switch (mType) {
                    case down: {
                        if (mTopHeight - Math.abs(curY) > mTopHeight / 6) {
                            animateShowTopView(curY);
                        } else {
                            animateHideTopView(curY);
                        }
                    }
                    break;
                    case up: {
                        if (Math.abs(curY) > mTopHeight / 6) {
                            animateHideTopView(curY);
                        } else {
                            animateShowTopView(curY);
                        }
                    }
                    break;
                }

                mType = null;
                mIsIntercept = false;
            }
            break;
            case MotionEvent.ACTION_CANCEL: {
                mType = null;
                mIsIntercept = false;
            }
            break;
        }

        return super.onTouchEvent(ev);
    }

    private void animateTopView(final float currY, final boolean showOrHide) {
        final float offset = Math.abs(showOrHide ? currY : mTopHeight - currY);

        ValueAnimator animator = ValueAnimator.ofFloat(offset).setDuration((int) offset / 2);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mListener != null) {
                    mListener.onAnimationStart();
                }

                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (showOrHide) {
                    setScrollY(0);
                } else {
                    setScrollY(mTopHeight);
                }

                if (mListener != null) {
                    mListener.onAnimationEnd(showOrHide ? TCompoundTopState.show : TCompoundTopState.hide);
                }

                mIsAnimating = false;
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float y = (Float) valueAnimator.getAnimatedValue();
                if (showOrHide) {
                    setScrollY((int) (offset - y));
                } else {
                    setScrollY((int) (currY + y));
                }
            }
        });

        animator.start();
    }

    private void animateShowTopView(float currY) {
        animateTopView(currY, true);
    }

    private void animateHideTopView(float currY) {
        animateTopView(currY, false);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mListener != null) {
            mListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    /**
     * 设置监听,是否拦截等
     *
     * @param listener
     */
    public void setOnCompoundScrollListener(ICompoundScrollListener listener) {
        mListener = listener;
    }

    /**
     * 寻找头布局, 可为空
     *
     * @return
     */
    abstract protected View findTopView();

    /**
     * 寻找中间布局, 可为空
     *
     * @return
     */
    abstract protected View findMidView();

    /**
     * 寻找底部布局, 不可为空
     *
     * @return
     */
    abstract protected View findBottomView();

    /**
     * 获得顶部滑动的预留高度
     *
     * @return
     */
    protected int getTopScrollKeepHeight() {
        return 0;
    }

    public int getTopHeight() {
        return mTopHeight;
    }
}

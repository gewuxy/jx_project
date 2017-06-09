package lib.ys.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.LinearInterpolator;

import lib.ys.R;
import lib.ys.fitter.DpFitter;
import lib.ys.util.AnimateUtil;
import lib.ys.util.UIUtil;

/**
 * @author zhangshuo
 * @modify ys
 */
public class ToggleButton extends View implements OnClickListener {

    private static final int KDuration = 100;
    private static final int KDefaultColorCheck = Color.parseColor("#377bee");
    private static final int KDefaultColorUnCheck = Color.parseColor("#999999");

    private Path mRectPath;

    private RectF mRectF;
    private Path mEdgePath;
    private RectF mEdgeRectF;

    private int mEdgeStrokeWidth;
    private Paint mPaint;

    private Paint mCircleEdgeP;
    private int mRadius;
    private int mCircleCenterX;

    private int mCircleCenterY;
    private boolean mCheck = false;

    private boolean mIsMoving = false;
    private boolean mIsCallBack = true;

    private AnimatorUpdateListener mAnimListener;
    private float mInterpolation;
    private AnimatorListener mEndListener;

    private int mW;
    private int mH;

    private View mCurrView;

    private OnToggleChangedListener mListener;

    private int mColorCheck;
    private int mColorUnCheck;

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToggleButton);
        mColorCheck = a.getColor(R.styleable.ToggleButton_toggle_colorCheck, KDefaultColorCheck);
        mColorUnCheck = a.getColor(R.styleable.ToggleButton_toggle_colorUnCheck, KDefaultColorUnCheck);
        a.recycle();

        init();
    }

    public void setToggleState(boolean check) {
        setToggleState(check, true, true);
    }

    public void setToggleState(boolean check, boolean isCallBack) {
        setToggleState(check, isCallBack, true);
    }

    public void setToggleState(boolean check, boolean isCallBack, boolean smooth) {
        if (!mIsMoving) {
            mCheck = !check;
            mIsCallBack = isCallBack;
            mIsMoving = true;

            if (check) {
                mCircleEdgeP.setColor(mColorCheck);
                mInterpolation = 1;
                nativeStart(0, 1, smooth ? KDuration : 0);
            } else {
                mCircleEdgeP.setColor(mColorUnCheck);

                mInterpolation = 0;
                nativeStart(1, 0, smooth ? KDuration : 0);
            }
        }
    }

    public boolean isChecked() {
        return mCheck;
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        mAnimListener = new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mInterpolation = (Float) valueAnimator.getAnimatedValue();
                ViewCompat.postInvalidateOnAnimation(ToggleButton.this);
            }
        };

        mEndListener = new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                stop();
                if (mIsCallBack && mListener != null) {
                    mListener.onToggleChanged(mCurrView, mCheck);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        };

        mRectPath = new Path();
        mEdgePath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.FILL);

        mEdgeStrokeWidth = DpFitter.dp(1);
        mCircleEdgeP = new Paint();
        mCircleEdgeP.setColor(mColorUnCheck);
        mCircleEdgeP.setStrokeWidth(mEdgeStrokeWidth);

        setOnClickListener(this);

        if (getViewTreeObserver().isAlive()) {
            getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mW = getMeasuredWidth();
                    mH = getMeasuredHeight();

                    if (mW == 0 || mH == 0) {
                        return true;
                    }

                    mCircleCenterY = mH / 2;
                    mRadius = mCircleCenterY;

                    computeCenter(mInterpolation);

                    mRectF = new RectF(mEdgeStrokeWidth, mEdgeStrokeWidth, mW - mEdgeStrokeWidth, mH - mEdgeStrokeWidth);
                    mRectPath.addRoundRect(mRectF, mRadius - mEdgeStrokeWidth / 2, mRadius - mEdgeStrokeWidth / 2, Direction.CCW);

                    mEdgeRectF = new RectF(0, 0, mW, mH);
                    mEdgePath.addRoundRect(mEdgeRectF, mRadius, mRadius, Direction.CCW);

                    getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }

        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mW == 0 || mH == 0) {
            return;
        }

        UIUtil.setCanvasAntialias(canvas);

        canvas.drawPath(mEdgePath, mCircleEdgeP);

        mPaint.setColor(mColorCheck);
        canvas.drawPath(mRectPath, mPaint);

        //
        computeCenter(mInterpolation);
        int count = canvas.save();
        canvas.clipRect(mCircleCenterX, 0, mW, mH);
        canvas.scale(1 - mInterpolation, 1 - mInterpolation, mW / 2, mCircleCenterY/*(实际上是mHeight / 2)*/);
        mPaint.setColor(Color.WHITE);
        canvas.drawPath(mRectPath, mPaint);
        canvas.restoreToCount(count);

        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mRadius, mCircleEdgeP);

        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mRadius - mCircleEdgeP.getStrokeWidth(), mPaint);
    }

    private void computeCenter(float interpolation) {
        mCircleCenterX = mRadius + (int) ((mW - mRadius * 2) * interpolation);
    }

    private void nativeStart(float from, float to, int duration) {
        ValueAnimator animator = AnimateUtil.ofFloatValue(from, to, duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(mAnimListener);
        animator.addListener(mEndListener);
        animator.start();
    }

    public void stop() {
        mIsMoving = false;
        mCheck = !mCheck;
    }

    public interface OnToggleChangedListener {
        void onToggleChanged(View view, boolean checked);
    }

    public void setOnToggleChangedListener(OnToggleChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        mCurrView = view;
        setToggleState(!mCheck, true, true);
    }

}

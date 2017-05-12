package lib.ys.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import lib.ys.R;
import lib.ys.timeTick.OnTimeTickListener;
import lib.ys.timeTick.TimeInterpolator;
import lib.ys.timeTick.TimeInterpolator.InterpolatorType;
import lib.ys.timeTick.TimeTicker;

/**
 * 动画形式绘制扇形
 */
public class AnimArcView extends View implements OnTimeTickListener {

    private static final long KDuration = 500;

    private Paint mPaintArc;
    private Paint mPaintBg;

    private RectF mArc;
    private int mRadius;
    private int mStrokeWidth;
    private int mStrokeColor;
    private boolean mUseAnim;

    // 背景
    private boolean mDrawBg;
    private int mStrokeBgColor;

    private int mSweepAngle = 0; // 圆环的长度
    private int mStartAngle = 0;
    private int mCurrSweepAngle = 0;

    private int mW;
    private int mH;

    private TimeInterpolator mTimeInterpolator;
    private TimeTicker mTimeTicker;

    public AnimArcView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.AnimArcView);
        mStrokeWidth = typeArray.getDimensionPixelOffset(R.styleable.AnimArcView_arc_strokeWidth, 1);
        mStrokeColor = typeArray.getColor(R.styleable.AnimArcView_arc_strokeColor, Color.WHITE);
        mStartAngle = typeArray.getInt(R.styleable.AnimArcView_arc_startAngle, 0);
        mSweepAngle = typeArray.getInt(R.styleable.AnimArcView_arc_sweepAngle, 360);

        mUseAnim = typeArray.getBoolean(R.styleable.AnimArcView_arc_useAnim, true);

        mStrokeBgColor = typeArray.getColor(R.styleable.AnimArcView_arc_strokeBgColor, Color.TRANSPARENT);
        mDrawBg = typeArray.getBoolean(R.styleable.AnimArcView_arc_drawBg, false);

        typeArray.recycle();

        init();
    }

    private void init() {
        mTimeInterpolator = TimeInterpolator.create(InterpolatorType.linear);
        mTimeTicker = new TimeTicker(this);

        mPaintArc = new Paint();
        mPaintArc.setAntiAlias(true);
        mPaintArc.setColor(mStrokeColor);
        mPaintArc.setStrokeWidth(mStrokeWidth);
        mPaintArc.setStyle(Style.STROKE);

        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(true);
        mPaintBg.setColor(mStrokeBgColor);
        mPaintBg.setStrokeWidth(mStrokeWidth);
        mPaintBg.setStyle(Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mW = getMeasuredWidth();
        mH = getMeasuredHeight();

        int xCenter = mW / 2;
        int yCenter = mH / 2;

        mRadius = xCenter > yCenter ? yCenter : xCenter;
        mRadius -= mStrokeWidth / 2;
        mArc = new RectF(xCenter - mRadius, yCenter - mRadius, xCenter + mRadius, yCenter + mRadius);

        if (mUseAnim) {
            mCurrSweepAngle = 0;
        } else {
            mCurrSweepAngle = mSweepAngle;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawBg) {
            canvas.drawCircle(mW / 2, mH / 2, mRadius, mPaintBg);
        }

        canvas.drawArc(mArc, mStartAngle, mCurrSweepAngle, false, mPaintArc);
    }

    @Override
    public void onTimeTick() {
        float interpolation = mTimeInterpolator.getInterpolation();
        mCurrSweepAngle = (int) (mSweepAngle * interpolation);

        invalidate();

        if (interpolation == TimeInterpolator.KMax) {
            mTimeTicker.stop();
        }
    }

    public void setSweepAngle(int angle) {
        mSweepAngle = angle;
    }

    public void setStartAngle(int angle) {
        mStartAngle = angle;
    }

    /**
     * 启动圈动画
     */
    public void start() {
        start(KDuration);
    }

    /**
     * 启动圈动画
     *
     * @param duration
     */
    public void start(long duration) {
        mCurrSweepAngle = 0;
        mTimeInterpolator.start(duration);
        mTimeTicker.start();
    }

    public void reset() {
        mTimeTicker.stop();
        mCurrSweepAngle = 0;
        invalidate();
    }
}

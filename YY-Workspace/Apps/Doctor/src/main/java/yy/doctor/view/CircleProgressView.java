package yy.doctor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import lib.ys.fitter.DpFitter;
import lib.ys.util.res.ResLoader;
import yy.doctor.R;

public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressBar";

    private final int mCircleLineStrokeWidth = DpFitter.dp(5);

    // 画圆所在的距形区域
    private final RectF mRectF;
    private final Paint mPaint;

    private int mProgress = 30;
    private int mMaxProgress = 100;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRectF = new RectF();
        mPaint = new Paint();

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.CircleProgressView);
        int color = ta.getColor(R.styleable.CircleProgressView_backProgress, ResLoader.getColor(R.color.divider));
        int width = ta.getInt(R.styleable.CircleProgressView_width, mCircleLineStrokeWidth);
        ta.recycle();

        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        mPaint.setStyle(Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int min = Math.min(getMeasuredWidth(), getMeasuredHeight());

        // 位置
        mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
        mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
        mRectF.right = min - mCircleLineStrokeWidth / 2; // 左下角x
        mRectF.bottom = min - mCircleLineStrokeWidth / 2; // 右下角y
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT);
        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        //绘制进度条
        mPaint.setColor(ResLoader.getColor(R.color.text_0882e7));
        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

}
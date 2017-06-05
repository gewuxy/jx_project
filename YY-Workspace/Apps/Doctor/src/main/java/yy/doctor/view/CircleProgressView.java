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
import lib.ys.util.XmlAttrUtil;
import lib.ys.util.res.ResLoader;
import yy.doctor.R;

public class CircleProgressView extends View {

    private final String TAG = getClass().getSimpleName();

    private static final int KLineW = 5; // 默认进度条宽度
    private static final int KBackColor = ResLoader.getColor(R.color.divider); // 默认进度条背景
    private static final int KProgressColor = ResLoader.getColor(R.color.text_0882e7); // 默认进度条进度颜色

    // 画圆所在的距形区域
    private final RectF mRectF;
    private final Paint mPaint;

    private final int mProgressColor; // 进度条的颜色
    private int mLineW; // 进度条的宽

    private int mProgress = 30;
    private int mMaxProgress = 100;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.CircleProgressView);
        int color = ta.getColor(R.styleable.CircleProgressView_circle_backProgress, KBackColor);
        mProgressColor = ta.getColor(R.styleable.CircleProgressView_circle_progress, KProgressColor);
        mLineW = ta.getDimensionPixelOffset(R.styleable.CircleProgressView_circle_width, KLineW);
        ta.recycle();

        mLineW = XmlAttrUtil.convert(mLineW, KLineW);

        mRectF = new RectF();
        mPaint = new Paint();

        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(mLineW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int min = Math.min(getMeasuredWidth(), getMeasuredHeight());

        // 位置
        mRectF.left = mLineW / 2; // 左上角x
        mRectF.top = mLineW / 2; // 左上角y
        mRectF.right = min - mLineW / 2; // 左下角x
        mRectF.bottom = min - mLineW / 2; // 右下角y
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT);
        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        //绘制进度条
        mPaint.setColor(mProgressColor);
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
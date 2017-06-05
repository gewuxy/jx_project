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

    private int mProgress = 0;
    private static final float KMaxProgress = 100;
    private boolean mIsDrawBgCircle = false;
    private int color;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        color = ta.getColor(R.styleable.CircleProgressView_circle_backProgress, KBackColor);
        mProgressColor = ta.getColor(R.styleable.CircleProgressView_circle_progress, KProgressColor);
        mLineW = ta.getDimensionPixelOffset(R.styleable.CircleProgressView_circle_widthDp, 0);
        ta.recycle();

        mLineW = XmlAttrUtil.convert(mLineW, KLineW);

        mRectF = new RectF();
        mPaint = new Paint();

        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
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
        mPaint.setColor(color);
        canvas.drawArc(mRectF, -90, 360, false, mPaint);

        //绘制进度条
        mPaint.setColor(mProgressColor);
        canvas.drawArc(mRectF, -90, (mProgress / KMaxProgress) * 360, false, mPaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

}
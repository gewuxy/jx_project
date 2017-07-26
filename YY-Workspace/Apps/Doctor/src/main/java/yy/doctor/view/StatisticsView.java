package yy.doctor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Map;

import lib.ys.YSLog;
import lib.ys.fitter.DpFitter;
import lib.ys.util.DrawUtil;
import lib.ys.util.res.ResLoader;
import yy.doctor.R;

/**
 * @auther : GuoXuan
 * @since : 2017/7/25
 */
public class StatisticsView extends View {

    private static final String TAG = StatisticsView.class.getSimpleName().toString();

    private final float KPercentLayoutHeight = 195f;
    private final float KPercentLineWidth = 4 / KPercentLayoutHeight;
    private final float KPercentLineMarginTop = 40 / KPercentLayoutHeight; // 柱状的上边距(相对应父控件)
    private final float KPercentLineMarginBottom = 33 / KPercentLayoutHeight; // 柱状的底边距(相对应父控件)
    private final float KPercentDividerMargin = 12 / KPercentLayoutHeight; // 分割线上下的外边距
    private final float KPercentTextMarginBottom = 16 / KPercentLayoutHeight; // 文本的底边距(显示数量的)
    private final float KPercentTextSize = 9 / KPercentLayoutHeight;

    private final int KRecColor = R.color.text_0882e7;
    private final int KDividerColor = R.color.divider;
    private final int KTextColor = R.color.text_888;

    private float mLineWidth;
    private float mLineMarginTop;
    private float mLineMarginBottom;
    private float mDividerMargin;
    private float mTextMarginBottom;
    private float mTextSize;

    private int mLayoutWidth; // 控件宽度
    private int mLayoutHeight; // 控件高度
    private int mMaxMeetNum; // 最大会议数
    private Map<String, Integer> mMeets; // 日期和会议数量
    private Paint mPaint;
    private int mCheckPosition; // 点击的position
    private int mRecColor; // 柱状图的颜色

    public StatisticsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatisticsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StatisticsView);
        mRecColor = ta.getColor(R.styleable.StatisticsView_rec_color, ResLoader.getColor(KRecColor));
        ta.recycle();

        mPaint = new Paint();
        mCheckPosition = Integer.MIN_VALUE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mLayoutWidth = getMeasuredWidth();
        mLayoutHeight = getMeasuredHeight();

        mLineWidth = KPercentLineWidth * mLayoutHeight;
        mLineMarginTop = KPercentLineMarginTop * mLayoutHeight;
        mLineMarginBottom = KPercentLineMarginBottom * mLayoutHeight;
        mDividerMargin = KPercentDividerMargin * mLayoutHeight;
        mTextMarginBottom = KPercentTextMarginBottom * mLayoutHeight;
        mTextSize = KPercentTextSize * mLayoutHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mMeets != null && !mMeets.isEmpty()) {
            int day = mMeets.size(); // 数量
            int width = mLayoutWidth / day; // 单条的宽度
            float maxHeight = mLayoutHeight - mLineMarginBottom - mLineMarginTop; // 单条最高的高度

            int position = 0; // 第几条
            int num; // 数量(一天的会议数)
            float posX;
            float posY;
            /******
             * 循环中只赋值一次
             */
            float top; // 柱状的顶点
            float bottom; // 柱状的底(部)点
            float left; // 单条的左
            float right; // 单条的右
            for (String date : mMeets.keySet()) {
                num = mMeets.get(date);

                posX = width * position + width / 2; // 单条的中心
                // 柱状

                mPaint.setStyle(Style.FILL);
                mPaint.setStrokeWidth(mLineWidth);
                mPaint.setStrokeCap(Cap.ROUND);
                mPaint.setColor(mRecColor);

                bottom = mLayoutHeight - mLineMarginBottom;
                top = bottom - num * maxHeight / mMaxMeetNum;
                if (num > 0) {
                    canvas.drawLine(posX, bottom, posX, top, mPaint);
                }
                mPaint.reset();

                // 分割线
                mPaint.setStyle(Style.FILL);
                mPaint.setColor(ResLoader.getColor(KDividerColor));

                left = position * width;
                right = left + width;
                posY = bottom + mDividerMargin;
                canvas.drawLine(left, posY, right, posY, mPaint);
                mPaint.reset();

                // 字体
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                mPaint.setAntiAlias(true);
                mPaint.setTextSize(mTextSize);
                mPaint.setColor(ResLoader.getColor(KTextColor));

                // 日期
                posY = mLayoutHeight - mTextSize - DpFitter.dp(2); // 高度不是字体大少
                DrawUtil.drawTextByAlignX(canvas, date, posX, posY, mPaint, Align.CENTER);
                // 数量
                if (mCheckPosition == position) {
                    posY = top - mTextSize - mTextMarginBottom;
                    DrawUtil.drawTextByAlignX(canvas, String.valueOf(num), posX, posY, mPaint, Align.CENTER);
                }
                mPaint.reset();

                YSLog.d(TAG, "onDraw:" + position + "---------------------------------------");
                position++;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                if (mMeets != null) {
                    mCheckPosition = (int) (event.getX() / (mLayoutWidth / mMeets.size()));
                }
                invalidate();
            }
            break;
        }
        return super.onTouchEvent(event);
    }

    public void setMeets(Map<String, Integer> meets) {
        mMeets = meets;

        mMaxMeetNum = Integer.MIN_VALUE;
        for (Integer value : meets.values()) {
            mMaxMeetNum = Math.max(value, mMaxMeetNum);
        }

        invalidate();
    }
}

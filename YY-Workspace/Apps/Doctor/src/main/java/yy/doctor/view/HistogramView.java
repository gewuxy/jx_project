package yy.doctor.view;

import android.content.Context;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lib.ys.YSLog;
import lib.ys.fitter.DpFitter;
import lib.ys.util.DrawUtil;
import lib.ys.util.res.ResLoader;
import yy.doctor.R;
import yy.doctor.model.me.Stats;
import yy.doctor.model.me.Stats.TStatistics;
import yy.doctor.model.me.StatsPerDay;
import yy.doctor.model.me.StatsPerDay.TStatsPerDay;

/**
 * 柱状图
 *
 * @auther : GuoXuan
 * @since : 2017/7/25
 */
public class HistogramView extends View {

    private static final String TAG = HistogramView.class.getSimpleName().toString();

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

    private boolean mCheck; // 是否为点击事件

    public void setRecColor(int recColor) {
        mRecColor = recColor;
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRecColor = KRecColor;
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
            float top; // 柱状的顶点
            float bottom = mLayoutHeight - mLineMarginBottom; // 柱状的底(部)点
            for (String date : mMeets.keySet()) {
                num = mMeets.get(date);

                posX = width * position + width / 2; // 单条的中心

                // 柱状
                mPaint.setStyle(Style.FILL);
                mPaint.setStrokeWidth(mLineWidth);
                mPaint.setStrokeCap(Cap.ROUND);
                mPaint.setColor(mRecColor);

                top = bottom - num * maxHeight / mMaxMeetNum;
                if (num > 0) {
                    canvas.drawLine(posX, bottom, posX, top, mPaint);
                }
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
            // 分割线
            mPaint.setStyle(Style.FILL);
            mPaint.setColor(ResLoader.getColor(KDividerColor));

            posY = bottom + mDividerMargin;
            canvas.drawLine(0, posY, mLayoutWidth, posY, mPaint);
            mPaint.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mCheck = true;
            }
            return true;
            case MotionEvent.ACTION_MOVE: {
                if (mCheck) {
                    mCheck = false;
                }
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (mCheck) {
                    if (mMeets != null) {
                        int position = (int) (event.getX() / (mLayoutWidth / mMeets.size()));
                        if (position != mCheckPosition) {
                            // 不是点击同一个
                            mCheckPosition = position;
                            invalidate();
                        }
                    }
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setMeets(Stats meets) {

        if (meets == null) {
            return;
        }

        // 重置数据
        mMeets = new LinkedHashMap<>();
        mCheckPosition = Integer.MIN_VALUE;

        List<StatsPerDay> l = meets.getList(TStatistics.list);
        if (l == null) {
            return;
        }

        mMaxMeetNum = Integer.MIN_VALUE;
        for (StatsPerDay stats : l) {
            int i = stats.getInt(TStatsPerDay.num, 0);
            mMaxMeetNum = Math.max(i, mMaxMeetNum);
            mMeets.put(stats.getString(TStatsPerDay.date), i);
        }

        invalidate();
    }

    /*@Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mMeets != null) {
            mMeets.clear();
        }
    }*/
}

package yy.doctor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

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

    private final int KLineWidth = DpFitter.dp(4);
    private final int KLineMarginTop = DpFitter.dp(40); // 柱状的上边距(相对应父控件)
    private final int KLineMarginBottom = DpFitter.dp(33); // 柱状的底边距(相对应父控件)
    private final int KDividerMargin = DpFitter.dp(12); // 分割线上下的外边距
    private final int KTextMarginBottom = DpFitter.dp(16); // 文本的底边距(显示数量的)
    private final int KTextSize = DpFitter.dp(9);

    private final int KRecColor = R.color.text_0882e7;
    private final int KDividerColor = R.color.divider;
    private final int KTextColor = R.color.text_888;

    private int mRecColor; // 柱状图的颜色
    private int mLayoutWidth; // 控件宽度
    private int mLayoutHeight; // 控件高度

    private List<StatsPerDay> mPerDays; // 日期和会议数量
    private List<Float> mHeights; // 柱状高度
    private List<Float> mMiddles; // 柱状中心

    private int mMaxMeetNum; // 最大会议数
    private int mCheckPosition; // 点击的position
    private boolean mCheck; // 是否为点击事件

    private Paint mPaintRec;
    private Paint mPaintText;
    private Paint mPaintDivider;

    public void setRecColor(@ColorRes int recColor) {
        mPaintRec.setColor(ResLoader.getColor(recColor));
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    /**
     * 初始化(画笔准备)
     */
    private void init() {
        mRecColor = KRecColor;
        mCheckPosition = Integer.MIN_VALUE; // 非法值
        mHeights = new ArrayList<>();
        mMiddles = new ArrayList<>();

        mPaintDivider = new Paint();
        mPaintDivider.setStyle(Style.FILL);
        mPaintDivider.setColor(ResLoader.getColor(KDividerColor));

        mPaintText = new Paint();
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(KTextSize);
        mPaintText.setColor(ResLoader.getColor(KTextColor));

        mPaintRec = new Paint();
        mPaintRec.setStyle(Style.FILL);
        mPaintRec.setStrokeWidth(KLineWidth);
        mPaintRec.setStrokeCap(Cap.ROUND);
        mPaintRec.setColor(ResLoader.getColor(mRecColor));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mLayoutWidth = getMeasuredWidth();
        mLayoutHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPerDays != null) {
            compute();

            drawRec(canvas);
            drawText(canvas);
            drawDivider(canvas);
        }
    }

    private void compute() {
        int day = mPerDays.size(); // 数量
        int width = mLayoutWidth / day; // 单个的宽度
        float maxHeight = mLayoutHeight - KLineMarginBottom - KLineMarginTop; // 单个最高的高度

        int num = 0; // 数量(一天的会议数)
        float posX;
        float posY;
        int bottom = mLayoutHeight - KLineMarginBottom; // 柱状的底(部)点
        for (int i = 0; i < day; i++) {
            num = mPerDays.get(i).getInt(TStatsPerDay.count, 0);
            posX = width * i + width / 2; // 单个的中心
            mMiddles.add(posX);
            posY = bottom - num * maxHeight / mMaxMeetNum;
            mHeights.add(posY);
        }
    }

    /**
     * 柱状
     *
     * @param canvas
     */
    private void drawRec(Canvas canvas) {
        int num = 0; // 数量(一天的会议数)
        int bottom = mLayoutHeight - KLineMarginBottom; // 柱状的底(部)点
        for (int i = 0; i < mPerDays.size(); i++) {
            num = mPerDays.get(i).getInt(TStatsPerDay.count, 0);
            if (num > 0) {
                canvas.drawLine(mMiddles.get(i), bottom, mMiddles.get(i), mHeights.get(i), mPaintRec);
            }
        }
    }

    /**
     * 文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        StatsPerDay statsPerDay;
        int posY = mLayoutHeight - KTextSize - DpFitter.dp(2); // 高度不是字体大少
        for (int i = 0; i < mPerDays.size(); i++) {
            statsPerDay = mPerDays.get(i);
            // 日期
            DrawUtil.drawTextByAlignX(canvas, statsPerDay.getString(TStatsPerDay.attendDate), mMiddles.get(i), posY, mPaintText, Align.CENTER);
            // 数量
            if (mCheckPosition == i) {
                float pos = mHeights.get(i) - KTextSize - KTextMarginBottom;
                DrawUtil.drawTextByAlignX(canvas, String.valueOf(statsPerDay.getInt(TStatsPerDay.count, 0)), mMiddles.get(i), pos, mPaintText, Align.CENTER);
            }
        }
    }

    /**
     * 分割线
     *
     * @param canvas
     */
    private void drawDivider(Canvas canvas) {
        int pos = mLayoutHeight - KLineMarginBottom + KDividerMargin;
        canvas.drawLine(0, pos, mLayoutWidth, pos, mPaintDivider);
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
                    if (mPerDays != null) {
                        int position = (int) (event.getX() / (mLayoutWidth / mPerDays.size()));
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

    public void setStats(Stats stats) {
        if (stats == null) {
            return;
        }

        // 重置数据
        mCheckPosition = Integer.MIN_VALUE;

        mPerDays = stats.getList(TStatistics.detailList);
        if (mPerDays == null) {
            return;
        }

        mMaxMeetNum = Integer.MIN_VALUE;
        for (StatsPerDay perDay : mPerDays) {
            mMaxMeetNum = Math.max(perDay.getInt(TStatsPerDay.count, 0), mMaxMeetNum);
        }

        invalidate();
    }
}

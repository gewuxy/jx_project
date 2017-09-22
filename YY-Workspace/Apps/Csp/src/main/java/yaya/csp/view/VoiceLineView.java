package yaya.csp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import yaya.csp.R;

/**
 * Created by carlos on 2016/1/29.
 * 自定义声音振动曲线view
 */
public class VoiceLineView extends View {

    private final int KLINE = 0;
    private final int KRECT = 1;

    private int KMiddleLineColor = Color.BLACK;
    private int KVoiceLineColor = Color.BLACK;
    private float KMiddleLineHeight = 4;

    private Paint mPaint;
    private Paint mPaintVoiceLine;
    private int mMode;
    /**
     * 灵敏度
     */
    private int mSensibility = 4;
    private float mMaxVolume = 100;
    private float mTranslateX = 0;
    private boolean mIsSet = false;
    /**
     * 振幅
     */
    private float mAmplitude = 1;
    /**
     * 音量
     */
    private float mVolume = 10;
    private int mFineness = 1;
    private float mTargetVolume = 1;

    private long mSpeedY = 50;
    private float mRectWidth = 25;
    private float mRectSpace = 5;
    private float mRectInitHeight = 4;
    private List<Rect> mRectList;

    private long mLastTime = 0;
    private int mLineSpeed = 90;

    List<Path> mPaths = null;

    public VoiceLineView(Context context) {
        super(context);
    }

    public VoiceLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtts(context, attrs);
    }

    private void initAtts(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VoiceView);
        mMode = typedArray.getInt(R.styleable.VoiceView_viewMode, 0);
        KVoiceLineColor = typedArray.getColor(R.styleable.VoiceView_voiceLine, Color.BLACK);
        mMaxVolume = typedArray.getFloat(R.styleable.VoiceView_maxVolume, 100);
        mSensibility = typedArray.getInt(R.styleable.VoiceView_sensibility, 4);
        if (mMode == KRECT) {
            mRectWidth = typedArray.getDimension(R.styleable.VoiceView_rectWidth, 25);
            mRectSpace = typedArray.getDimension(R.styleable.VoiceView_rectSpace, 5);
            mRectInitHeight = typedArray.getDimension(R.styleable.VoiceView_rectInitHeight, 4);
        } else {
            KMiddleLineColor = typedArray.getColor(R.styleable.VoiceView_middleLine, Color.BLACK);
            KMiddleLineHeight = typedArray.getDimension(R.styleable.VoiceView_middleLineHeight, 4);
            mLineSpeed = typedArray.getInt(R.styleable.VoiceView_lineSpeed, 90);
            mFineness = typedArray.getInt(R.styleable.VoiceView_fineness, 1);
            mPaths = new ArrayList<>(10);
            for (int i = 0; i < 10; i++) {
                mPaths.add(new Path());
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMode == KRECT) {
            drawVoiceRect(canvas);
        } else {
            drawMiddleLine(canvas);
            drawVoiceLine(canvas);
        }
        run();
    }

    /**
     * 画中间线
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(KMiddleLineColor);
            mPaint.setAntiAlias(true);
        }
        canvas.save();
        canvas.drawRect(0, getHeight() / 2 - KMiddleLineHeight / 2, getWidth(), getHeight() / 2 + KMiddleLineHeight / 2, mPaint);
        canvas.restore();
    }

    /**
     * 画线型声波
     * @param canvas
     */
    private void drawVoiceLine(Canvas canvas) {
        lineChange();
        if (mPaintVoiceLine == null) {
            mPaintVoiceLine = new Paint();
            mPaintVoiceLine.setColor(KVoiceLineColor);
            mPaintVoiceLine.setAntiAlias(true);
            mPaintVoiceLine.setStyle(Paint.Style.STROKE);
            mPaintVoiceLine.setStrokeWidth(2);
        }
        canvas.save();
        int moveY = getHeight() / 2;
        for (int i = 0; i < mPaths.size(); i++) {
            mPaths.get(i).reset();
            mPaths.get(i).moveTo(getWidth(), getHeight() / 2);
        }
        for (float i = getWidth() - 1; i >= 0; i -= mFineness) {
            mAmplitude = 4 * mVolume * i / getWidth() - 4 * mVolume * i * i / getWidth() / getWidth();
            for (int n = 1; n <= mPaths.size(); n++) {
                float sin = mAmplitude * (float) Math.sin((i - Math.pow(1.22, n)) * Math.PI / 180 - mTranslateX);
                mPaths.get(n - 1).lineTo(i, (2 * n * sin / mPaths.size() - 15 * sin / mPaths.size() + moveY));
            }
        }
        for (int n = 0; n < mPaths.size(); n++) {
            if (n == mPaths.size() - 1) {
                mPaintVoiceLine.setAlpha(255);
            } else {
                mPaintVoiceLine.setAlpha(n * 130 / mPaths.size());
            }
            if (mPaintVoiceLine.getAlpha() > 0) {
                canvas.drawPath(mPaths.get(n), mPaintVoiceLine);
            }
        }
        canvas.restore();
    }

    /**
     * 画曲线声波
     * @param canvas
     */
    private void drawVoiceRect(Canvas canvas) {
        if (mPaintVoiceLine == null) {
            mPaintVoiceLine = new Paint();
            mPaintVoiceLine.setColor(KVoiceLineColor);
            mPaintVoiceLine.setAntiAlias(true);
            mPaintVoiceLine.setStyle(Paint.Style.STROKE);
            mPaintVoiceLine.setStrokeWidth(2);
        }
        if (mRectList == null) {
            mRectList = new LinkedList<>();
        }
        int totalWidth = (int) (mRectSpace + mRectWidth);
        if (mSpeedY % totalWidth < 6) {
            Rect rect = new Rect((int) (-mRectWidth - 10 - mSpeedY + mSpeedY % totalWidth),
                    (int) (getHeight() / 2 - mRectInitHeight / 2 - (mVolume == 10 ? 0 : mVolume / 2)),
                    (int) (-10 - mSpeedY + mSpeedY % totalWidth),
                    (int) (getHeight() / 2 + mRectInitHeight / 2 + (mVolume == 10 ? 0 : mVolume / 2)));
            if (mRectList.size() > getWidth() / (mRectSpace + mRectWidth) + 2) {
                mRectList.remove(0);
            }
            mRectList.add(rect);
        }
        canvas.translate(mSpeedY, 0);
        for (int i = mRectList.size() - 1; i >= 0; i--) {
            canvas.drawRect(mRectList.get(i), mPaintVoiceLine);
        }
        rectChange();
    }

    public void setVolume(int volume) {
        if (volume > mMaxVolume * mSensibility / 25) {
            mIsSet = true;
            this.mTargetVolume = getHeight() * volume / 2 / mMaxVolume;
        }
    }

    private void lineChange() {
        if (mLastTime == 0) {
            mLastTime = System.currentTimeMillis();
            mTranslateX += 1.5;
        } else {
            if (System.currentTimeMillis() - mLastTime > mLineSpeed) {
                mLastTime = System.currentTimeMillis();
                mTranslateX += 1.5;
            } else {
                return;
            }
        }
        if (mVolume < mTargetVolume && mIsSet) {
            mVolume += getHeight() / 30;
        } else {
            mIsSet = false;
            if (mVolume <= 10) {
                mVolume = 10;
            } else {
                if (mVolume < getHeight() / 30) {
                    mVolume -= getHeight() / 60;
                } else {
                    mVolume -= getHeight() / 30;
                }
            }
        }
    }

    private void rectChange() {
        mSpeedY += 6;
        if (mVolume < mTargetVolume && mIsSet) {
            mVolume += getHeight() / 30;
        } else {
            mIsSet = false;
            if (mVolume <= 10) {
                mVolume = 10;
            } else {
                if (mVolume < getHeight() / 30) {
                    mVolume -= getHeight() / 60;
                } else {
                    mVolume -= getHeight() / 30;
                }
            }
        }
    }

    public void run() {
        if (mMode == KRECT) {
            postInvalidateDelayed(30);
        } else {
            invalidate();
        }
    }

}
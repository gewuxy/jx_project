package lib.ys.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import lib.ys.ConstantsEx;
import lib.ys.fitter.DpFitter;


public class SideBar extends View {

    private final int KPaintColorNormal = Color.parseColor("#616161");
    private final int KPaintColorFocus = Color.parseColor("#F88701");
    private final int KDefaultTextSizeDp = 11;

    @ColorInt
    private int mColorNormal = KPaintColorNormal;

    @ColorInt
    private int mColorFocus = KPaintColorFocus;

    private OnTouchLetterListener mListener;

    private Paint mPaint;
    private int mChoose = ConstantsEx.KErrNotFound;

    // 每个字母的高度
    private int mSingleHeight;
    private int mRealHeight;

    private int mTextSize;

    private boolean mCustomLetterHeight;

    private int mWidth;
    private int mHeight;

    // 准备好的A~Z的字母数组
    public String[] mLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTextSize = DpFitter.dp(KDefaultTextSizeDp);
        mPaint = new Paint();
    }

    public void setData(String[] letters) {
        mLetters = letters;
        if (!mCustomLetterHeight) {
            mSingleHeight = mHeight / mLetters.length;
        }
        computeRealHeight();

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mSingleHeight = mHeight / mLetters.length;
        computeRealHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画字母
        for (int i = 0; i < mLetters.length; i++) {
            mPaint.setColor(mColorNormal);

            // 设置字体格式
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);

            // 如果这一项被选中，则换一种颜色画
            if (i == mChoose) {
                mPaint.setColor(mColorFocus);
                mPaint.setFakeBoldText(true);
            }
            // 要画的字母的x,y坐标
            float posX = (mWidth - getPaddingRight() - mPaint.measureText(mLetters[i])) / 2; // 右对齐再居中
            float posY = i * mSingleHeight + mSingleHeight;
            // 画出字母
            canvas.drawText(mLetters[i], posX, posY, mPaint);

            // 重新设置画笔
            mPaint.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float y = event.getY();
        // 算出点击的字母的索引
        final int index = (int) (y / mSingleHeight);
        // 保存上次点击的字母的索引到oldChoose
        final int oldChoose = mChoose;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // view有字母部分的高度，其他部分不接受touch事件
                if (y > mRealHeight) {
                    return false;
                }
                // showBg = true;
                if (oldChoose != index && mListener != null && index >= 0 && index < mLetters.length) {
                    mChoose = index;
                    mListener.onTouchLetterChanged(mLetters[index], true);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_MOVE: {
                if (oldChoose != index && mListener != null && index >= 0 && index < mLetters.length) {
                    mChoose = index;
                    mListener.onTouchLetterChanged(mLetters[index], true);
                    invalidate();
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mChoose = -1;
                if (mListener != null) {
                    if (index <= 0) {
                        mListener.onTouchLetterChanged(mLetters[0], false);
                    } else if (index >= 0 && index < mLetters.length) {
                        mListener.onTouchLetterChanged(mLetters[index], false);
                    } else if (index >= mLetters.length) {
                        mListener.onTouchLetterChanged(mLetters[mLetters.length - 1], false);
                    }
                }
                invalidate();
            }
            break;
        }
        return true;
    }

    /**
     * 回调方法，注册监听器
     *
     * @param listener
     */
    public void setOnTouchLetterChangeListener(OnTouchLetterListener listener) {
        mListener = listener;
    }

    /**
     * SideBar 的监听器接口
     */
    public interface OnTouchLetterListener {
        void onTouchLetterChanged(String s, boolean isFocus);
    }

    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setColor(@ColorInt int color) {
        mColorNormal = color;
    }

    public void setColorFocus(@ColorInt int color) {
        mColorFocus = color;
    }

    /**
     * 设置每个字母的高度
     *
     * @param singleHeight
     */
    public void setSingleHeight(int singleHeight) {
        mCustomLetterHeight = true;
        mSingleHeight = singleHeight;
        computeRealHeight();
    }

    public void setTextSize(int size) {
        mTextSize = size;
    }

    private void computeRealHeight() {
        mRealHeight = mSingleHeight * mLetters.length;
    }
}
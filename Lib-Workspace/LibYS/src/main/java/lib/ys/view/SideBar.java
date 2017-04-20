package lib.ys.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import lib.ys.fitter.DpFitter;


public class SideBar extends View {

    private final int KPaintColorNormal = Color.parseColor("#616161");
    private final int KPaintColorFocus = Color.parseColor("#F88701");
    private int mPaintColor = KPaintColorNormal;

    private Paint mPaint = new Paint();
    private OnTouchLetterChangeListener mListener;
    private int mChoose = -1;
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
        mTextSize = DpFitter.dp(11);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 每个字母的高度
        if (mSingleHeight == 0) {
            mSingleHeight = mHeight / mLetters.length;
            computeRealHeight();
        }

        // 画字母
        for (int i = 0; i < mLetters.length; i++) {
            mPaint.setColor(mPaintColor);
            // 设置字体格式
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            // 如果这一项被选中，则换一种颜色画
            if (i == mChoose) {
                mPaint.setColor(KPaintColorFocus);
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
                    mListener.onTouchLetterChange(mLetters[index]);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_MOVE: {
                if (oldChoose != index && mListener != null && index >= 0 && index < mLetters.length) {
                    mChoose = index;
                    mListener.onTouchLetterChange(mLetters[index]);
                    invalidate();
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mChoose = -1;
                if (mListener != null) {
                    if (index <= 0) {
                        mListener.onTouchLetterChange(mLetters[0]);
                    } else if (index >= 0 && index < mLetters.length) {
                        mListener.onTouchLetterChange(mLetters[index]);
                    } else if (index >= mLetters.length) {
                        mListener.onTouchLetterChange(mLetters[mLetters.length - 1]);
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
    public void setOnTouchLetterChangeListener(OnTouchLetterChangeListener listener) {
        mListener = listener;
    }

    /**
     * SideBar 的监听器接口
     *
     * @author Folyd
     */
    public interface OnTouchLetterChangeListener {
        void onTouchLetterChange(String s);
    }

    /**
     * 设置画笔颜色
     *
     * @param paintColor
     */
    public void setPaintColor(int paintColor) {
        mPaintColor = paintColor;
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

    @Override
    public void invalidate() {
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
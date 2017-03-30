package lib.ys.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.TextView;

/**
 * 字体大小自动缩放的view, 暂时只适应于一行文字
 *
 * @author yuansui
 */
public class AutoScaleTextView extends TextView {

    private Paint mPaint;

    private boolean mInit = true;

    // 是否可放大
    private boolean mZoomInEnable = true;
    // 是否可缩小
    private boolean mZoomOutEnable = true;

    // 原有大小
    private float mSizeOrc;

    private boolean mUseInitSize = false;

    private String mTextCalc;

    public AutoScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (text.length() == 0) {
            return;
        }

        if (mUseInitSize) {
            if (mInit) {
                computeTextSize(mTextCalc);
                mInit = false;
            }
            return;
        }

        computeTextSize(text.toString());
    }

    private void init() {
        mPaint = new Paint();
        mSizeOrc = getTextSize();
    }

    /**
     * 字体自适应宽高
     *
     * @param text
     */
    private void computeTextSize(final String text) {
        if (text == null) {
            return;
        }

        if (getWidth() <= 0) {
            if (getViewTreeObserver().isAlive()) {
                getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        computeTextSize(text);
                        getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
            return;
        }

        String measureText = text;
        float size = getTextSize();
        mPaint.setTextSize(size);

        /**
         * 宽度自适应不做处理
         */
        // 获取宽比例
        float len = mPaint.measureText(measureText);
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        float scaleW = width / len;

        size = size * scaleW;
        // newSize计算出来只是一个近似值, 需要微调一下
        mPaint.setTextSize(size);
        len = mPaint.measureText(measureText);
        // 适应宽度
        while (len > width) {
            size -= 1;
            mPaint.setTextSize(size);
            len = mPaint.measureText(measureText);
        }

        // 计算文字高度
        mPaint.setTextSize(size);

        if (size > mSizeOrc && !mZoomInEnable) {
            // size放大时又不允许变大, 还原
            size = mSizeOrc;
        } else if (size < mSizeOrc && !mZoomOutEnable) {
            // size缩小时又不允许缩小, 还原
            size = mSizeOrc;
        }

        // 要记得使用COMPLEX_UNIT_PX方式, 默认为COMPLEX_UNIT_SP, 会有额外缩放
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    /**
     * 设置是否允许同时放大和缩小
     *
     * @param enable
     */
    public void setZoomEnable(boolean enable) {
        mZoomInEnable = enable;
        mZoomOutEnable = enable;
    }

    /**
     * 设置是否允许放大
     *
     * @param enable
     */
    public void setZoomInEnable(boolean enable) {
        mZoomInEnable = enable;
    }

    /**
     * 设置是否允许缩小
     *
     * @param enable
     */
    public void setZoomOutEnable(boolean enable) {
        mZoomOutEnable = enable;
    }

    /**
     * 设置字体希望保持的原有大小
     *
     * @param size
     */
    public void setOrcTextSize(float size) {
        mSizeOrc = size;
    }

    /**
     * 根据一开始设置好的文本调整大小, 后期文本就算改变也不重新调整
     *
     * @param text
     */
    public void useInitSize(String text) {
        mInit = true;
        mTextCalc = text;
        mUseInitSize = true;
    }

    public void unUseInitSize() {
        mUseInitSize = false;
    }
}

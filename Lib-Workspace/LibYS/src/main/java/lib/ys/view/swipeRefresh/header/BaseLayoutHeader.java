package lib.ys.view.swipeRefresh.header;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;

import lib.ys.view.swipeRefresh.base.BaseSRLayout;

/**
 * @author yuansui
 */
abstract public class BaseLayoutHeader extends BaseHeader {

    private int mOffset;
    private int mWidth;
    private int mContentHeight;

    public BaseLayoutHeader(Context context, BaseSRLayout layout) {
        super(context, layout);

        findViews();
        setViewsValue();

        if (getViewTreeObserver().isAlive()) {
            getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    /**
                     * 不能在{@link View#onMeasure(int, int)}里赋值
                     * SRLayout会经常调用此方法做更改
                     */
                    mContentHeight = getContentView().getHeight();
                    mOffset = -mContentHeight;
                    mWidth = getWidth();
                    if (mOffset == 0 || mWidth == 0) {
                        // PS: 这里返回值一定要为true
                        return true;
                    }

                    getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    @Override
    protected View initContentView() {
        return LayoutInflater.from(getContext()).inflate(getContentViewId(), null);
    }

    abstract protected int getContentViewId();

    abstract protected void findViews();

    abstract protected void setViewsValue();

    @Override
    public void offset(int offset) {
        mOffset += offset;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mWidth == 0) {
            return;
        }

        int count = canvas.save();

        /**
         * 方法1: 先位移后clip(不好理解)
         */
//        canvas.translate(0, mOffset);
//        canvas.clipRect(0, getPaddingTop() + mContentHeight, mWidth, getPaddingTop() + Math.abs(mOffset));
        /**
         * 方法2: 先clip后位移(好理解)
         */
//        canvas.clipRect(0, getPaddingTop(), mWidth, mContentHeight + getPaddingTop());
        canvas.clipRect(0, 0, mWidth, mContentHeight);
        canvas.translate(0, mOffset);

        super.dispatchDraw(canvas);

        canvas.restoreToCount(count);
    }

}

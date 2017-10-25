package yy.doctor.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @auther : GuoXuan
 * @since : 2017/10/25
 */

public class DispatchView extends LinearLayout {

    private boolean mDispatch;

    public DispatchView(Context context) {
        this(context, null);
    }

    public DispatchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DispatchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDispatch = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDispatch) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDispatch) {
            return true;
        } else  {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDispatch) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (!mDispatch) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    public void dispatchTouchEvent(boolean state) {
        mDispatch = state;
    }
}

package yy.doctor.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import lib.ys.YSLog;

/**
 * @auther : GuoXuan
 * @since : 2017/6/8
 */

public class RootLayout extends FrameLayout {

    private static final String TAG = "RootLayout";

    private OnRootTouchListener mOnRootTouchListener;

    public RootLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        YSLog.d("RootLayout", "point");
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (mOnRootTouchListener != null) {
                    mOnRootTouchListener.onTouchUp();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface OnRootTouchListener {
        void onTouchUp();
    }

    public void setOnRootTouchListener(OnRootTouchListener onRootTouchListener) {
        mOnRootTouchListener = onRootTouchListener;
    }
}

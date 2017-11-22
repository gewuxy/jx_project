package jx.csp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import lib.ys.YSLog;

/**
 * 监听左右滑动手势
 *
 * @author CaiXiang
 * @since 2017/10/12
 */

public class GestureView extends RelativeLayout {

    private GestureDetector mGestureDetector; // 手势监听
    private onGestureViewListener mListener;

    public GestureView(Context context) {
        super(context);
    }

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int distance = (int) (e2.getX() - e1.getX());
                YSLog.d("www", "onScroll distance = " + distance);
                if (distance > 0) {
                    mListener.moveLast();
                } else {
                    mListener.moveNext();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void setGestureViewListener(onGestureViewListener l) {
        mListener = l;
    }

    public interface onGestureViewListener {
        void moveLast();

        void moveNext();
    }

}

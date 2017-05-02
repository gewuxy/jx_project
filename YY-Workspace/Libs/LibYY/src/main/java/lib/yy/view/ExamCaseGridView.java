package lib.yy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 带空白区域监听的GridView(网上代码)
 * 判断触摸的位置是否为INVALID_POSITION(无效的position)
 *
 * @author : GuoXuan
 * @since : 2017/4/29
 */

public class ExamCaseGridView extends GridView {

    /**
     * 空白区域的监听器
     */
    public interface OnInvalidListener {
        /**
         * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者 MotionEvent.ACTION_UP等来按需要进行判断
         * @return 是否要终止事件的路由
         */
        boolean onInvalidPosition(int motionEvent);
    }

    private OnInvalidListener mInvalidListener;

    public ExamCaseGridView(Context context) {
        super(context);
    }

    public ExamCaseGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExamCaseGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 点击空白区域时的响应和处理接口
     */
    public void setOnInvalidListener(OnInvalidListener l) {
        mInvalidListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mInvalidListener == null) {
            return super.onTouchEvent(event);
        }

        if (!isEnabled()) {
            // A disabled view that is clickable still consumes the touch
            // events, it just doesn't respond to them.
            return isClickable() || isLongClickable();
        }

        final int motionPosition = pointToPosition((int)event.getX(), (int)event.getY());


        if( motionPosition == INVALID_POSITION ) {
            super.onTouchEvent(event);
            return mInvalidListener.onInvalidPosition(event.getActionMasked());
        }

        return super.onTouchEvent(event);
    }
}
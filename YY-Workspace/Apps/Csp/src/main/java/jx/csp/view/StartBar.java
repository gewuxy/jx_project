package jx.csp.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.model.main.Meet;
import jx.csp.ui.activity.main.StartActivityRouter;
import jx.csp.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2018/1/16
 */
public class StartBar extends RelativeLayout {

    private StartThumb mStartThumb;
    private SeekBar mSeekBar;
    private TextView mTvStart;

    private Meet mMeet;

    public StartBar(Context context) {
        this(context, null);
    }

    public StartBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StartBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        View layout = inflate(getContext(), R.layout.layout_start_bar, this);
        mSeekBar = layout.findViewById(R.id.seek_bar);
        mSeekBar.setOnTouchListener((v, event) -> true);
        mSeekBar.setPadding(0, 0, 0, 0);
        mStartThumb = layout.findViewById(R.id.start_thumb);
        mStartThumb.setListener(x -> mSeekBar.setProgress(x));
        mTvStart = layout.findViewById(R.id.start_tv);
    }

    /**
     * 设置文本
     *
     * @param c 启动星评,滑动结束
     */
    public void setText(CharSequence c) {
        mTvStart.setText(c);
    }

    /**
     * 跳转需要的数据
     *
     * @param meet 会议信息
     */
    public void setMeet(Meet meet) {
        mMeet = meet;
    }

    /**
     * 设置滑块
     *
     * @param resId 滑块的id
     */
    public void setThumb(@DrawableRes int resId) {
        mStartThumb.setThumb(resId);
    }

    /**
     * 设置滑动监听
     *
     * @param startListener Listener
     */
    public void setStartListener(StartThumb.OnStartListener startListener) {
        mStartThumb.setStartListener(() -> {
            if (startListener != null) {
                startListener.onClick();
            }
            if (mMeet != null) {
                StartActivityRouter.create(mMeet).route(getContext());
            }

            Util.runOnUIThread(this::restoration, 300);
        });
    }

    /**
     * 复位
     */
    public void restoration() {
        mStartThumb.setLocation(0);
        mSeekBar.setProgress(0);
    }

}

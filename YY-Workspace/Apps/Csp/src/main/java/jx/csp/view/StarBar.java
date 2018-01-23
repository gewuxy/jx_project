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

/**
 * @auther : GuoXuan
 * @since : 2018/1/16
 */
public class StarBar extends RelativeLayout {

    private StarThumb mStarThumb;
    private SeekBar mSeekBar;
    private TextView mTvStar;

    private Meet mMeet;

    public StarBar(Context context) {
        this(context, null);
    }

    public StarBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        View layout = inflate(getContext(), R.layout.layout_star_bar, this);
        mSeekBar = layout.findViewById(R.id.seek_bar);
        mSeekBar.setOnTouchListener((v, event) -> true);
        mSeekBar.setPadding(0, 0, 0, 0);
        mStarThumb = layout.findViewById(R.id.star_thumb);
        mStarThumb.setListener(x -> mSeekBar.setProgress(x));
        mTvStar = layout.findViewById(R.id.star_tv);
    }

    /**
     * 设置文本
     *
     * @param c 启动星评,滑动结束
     */
    public void setText(CharSequence c) {
        mTvStar.setText(c);
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
        mStarThumb.setThumb(resId);
    }

    /**
     * 设置滑动监听
     *
     * @param startListener Listener
     */
    public void setStartListener(StarThumb.OnStartListener startListener) {
        mStarThumb.setStartListener(startListener);
    }

    /**
     * 复位
     */
    public void restoration() {
        mStarThumb.setLocation(0);
        mSeekBar.setProgress(0);
    }

}

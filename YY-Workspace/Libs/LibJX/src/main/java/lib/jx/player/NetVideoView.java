package lib.jx.player;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.pili.pldroid.player.widget.PLVideoView;

import lib.ys.YSLog;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;


/**
 * 日期 : 2017/5/26
 * 创建人 : GuoXuan
 */

public class NetVideoView extends PLVideoView implements OnCountDownListener {

    private static final String TAG = NetVideoView.class.getSimpleName();

    private ViewGroup.LayoutParams mParams;
    private CountDown mCountDown;

    private VideoViewListener mListener;

    private long mRemainTime; // 剩余播放的时间(s)

    public interface VideoViewListener {
        /**
         * 播放进度
         *
         * @param progress 已播放时长
         */
        void onVideoProgress(long progress);
    }

    public NetVideoView(Context context) {
        super(context);

        init();
    }

    private void init() {
        getSurfaceView().setZOrderMediaOverlay(true);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mParams = getLayoutParams();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public void setOnProListener(VideoViewListener onProListener) {
        mListener = onProListener;
    }

    /**
     * 设置横屏
     */
    public void rotateLandscape() {
        mParams.width = LayoutParams.MATCH_PARENT;
        mParams.height = LayoutParams.MATCH_PARENT;
        setLayoutParams(mParams);
    }

    /**
     * 设置竖屏
     */
    public void rotatePortrait(int heightPx) {
        mParams.width = LayoutParams.MATCH_PARENT;
        mParams.height = heightPx;
        setLayoutParams(mParams);
    }

    /**
     * 转化播放状态
     *
     * @return true(播放), false(暂停)
     */
    public boolean toggleState() {
        //暂停
        if (isPlaying()) {
            pause();
            recycle();
            mRemainTime = (getDuration() - getCurrentPosition()) / 1000;
        } else {
            start();
            videoStart(mRemainTime);
        }
        return !isPlaying();
    }

    /**
     * 开始播放进度
     *
     * @param count
     */
    private void videoStart(final long count) {
        recycle();
        if (mCountDown == null) {
            mCountDown = new CountDown(count);
            mCountDown.setListener(this);
        }
        mCountDown.start();
    }

    /**
     * 准备的设置
     *
     * @param count
     */
    public void prepared(long count) {
        mRemainTime = count;
        videoStart(mRemainTime);
    }

    /**
     * 停止倒计时
     */
    public void recycle() {
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onCountDownErr() {
        YSLog.d(TAG, "onCountDownErr()");
    }

    @Override
    public void onCountDown(long remainCount) {
        if (mListener != null && isPlaying()) {
            mListener.onVideoProgress(getCurrentPosition() / 1000);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mListener = null;
        stopPlayback();
        recycle();
    }
}

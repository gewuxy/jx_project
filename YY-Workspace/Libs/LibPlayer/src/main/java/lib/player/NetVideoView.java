package lib.player;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.pili.pldroid.player.widget.PLVideoView;

import lib.ys.LogMgr;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

import static lib.ys.ui.interfaces.opts.FitOpt.MATCH_PARENT;

/**
 * 日期 : 2017/5/26
 * 创建人 : GuoXuan
 */

public class NetVideoView extends PLVideoView implements OnCountDownListener {

    private static final String TAG = NetVideoView.class.getSimpleName();
    
    private ViewGroup.LayoutParams mParams;
    private CountDown mCountDown;

    private VideoViewListener mListener;

    private long mCurrTime;

    private long mLastTime;//上一秒的时间
    private long mStopTime;//上次暂停的时间
    private long mRemainTime;//剩余的时间
    private boolean mHasStop;//暂停过

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
        mParams.width = MATCH_PARENT;
        mParams.height = MATCH_PARENT;
        setLayoutParams(mParams);
    }

    /**
     * 设置竖屏
     */
    public void rotatePortrait(int heightPx) {
        mParams.width = MATCH_PARENT;
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
            mHasStop = true;
            mStopTime = mLastTime;
        } else {
            start();
            proStart(mRemainTime);
        }
        return !isPlaying();
    }

    /**
     * 开始播放进度
     *
     * @param count
     */
    private void proStart(final long count) {
        recycle();
        if (mCountDown == null) {
//            mCountDown = new CountDown(count) {
//
//                @Override
//                public void onNext(Long aLong) {
//                    mRemainTime = aLong;
//                    if (mListener != null) {
//                        long time = count - aLong;
//                        mLastTime = mStopTime + time;//记录上次时间
//                        if (mHasStop) {//暂停过就加上暂停前播放的时候
//                            time += mStopTime;
//                        }
//                        mListener.onPro(time);
//                    }
//                }
//            };
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
    public void prepared(long count, long last) {
        mRemainTime = count;
        mLastTime = last;
        mStopTime = mLastTime;
        mHasStop = last > 0;
        proStart(count);
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
        LogMgr.d(TAG, "onCountDownErr()");
    }

    @Override
    public void onCountDown(long remainCount) {
        mRemainTime = remainCount;
        if (mListener != null) {
            LogMgr.d(TAG, "count = " + remainCount);
        }
//        if (mListener != null) {
//            long time = count - remainCount;
//            mLastTime = mStopTime + time;//记录上次时间
//            if (mHasStop) {//暂停过就加上暂停前播放的时候
//                time += mStopTime;
//            }
//            mListener.onPro(time);
//        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stopPlayback();

        if (mCountDown != null) {
            mCountDown.stop();
        }
    }
}

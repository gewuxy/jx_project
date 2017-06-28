package yy.doctor.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import java.util.concurrent.TimeUnit;

import lib.ys.YSLog;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * 单例播放器
 *
 * @auther : GuoXuan
 * @since : 2017/6/24
 */

public class AudioPlayer implements OnCompletionListener, OnCountDownListener {

    private static final String TAG = "AudioPlayer";

    private static AudioPlayer mAudioPlayer;
    private MediaPlayer mMp;
    private OnPlayerListener mPlayerListener;
    private CountDown mCountDown;
    private long mRemainTime; // 剩余播放时间 s

    public interface OnPlayerListener {
        /**
         * 播放进度
         * @param currMilliseconds 毫秒
         */
        void onProgress(long currMilliseconds);

        /**
         * 播放结束
         */
        void end();
    }

    private AudioPlayer() {
    }

    public void setPlayerListener(OnPlayerListener playerListener) {
        mPlayerListener = playerListener;
    }

    public synchronized static AudioPlayer inst() {
        if (mAudioPlayer == null) {
            mAudioPlayer = new AudioPlayer();
        }
        return mAudioPlayer;
    }

    public void start(String path) {
        if (mMp == null) {
            mMp = new MediaPlayer();
            mMp.setOnCompletionListener(this);
            mMp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            mMp.reset();
            mMp.setDataSource(path);
            mMp.prepare();
            mMp.start();
            mRemainTime = mMp.getDuration() / TimeUnit.SECONDS.toMillis(1);
            startCount();
        } catch (Exception e) {
            YSLog.e(TAG, "start", e);
        }
    }

    public void play() {
        if (mMp != null) {
            mMp.start();
            startCount();
        }
    }

    public void seekTo(int msec) {
        if (mMp != null) {
            mMp.seekTo(msec);
        }
        mRemainTime = mMp.getDuration() - msec;
        startCount();
    }

    public void pause() {
        if (mMp != null) {
            mMp.pause();
        }
        stopCount();
    }

    public void stop() {
        if (mMp != null) {
            mMp.reset();
            mMp = null;
            mRemainTime = 0;
        }
        stopCount();
    }

    public void release() {
        if (mMp != null) {
            mMp.release();
        }
    }

    /**
     * 开始计时
     */
    private void startCount() {
        stopCount();
        mCountDown = new CountDown(mRemainTime);
        mCountDown.setListener(this);
        mCountDown.start();
    }

    /**
     * 停止计时
     */
    private void stopCount() {
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onCountDown(long remainCount) {
        mRemainTime = remainCount;
        if (mMp != null && mPlayerListener != null) {
            mPlayerListener.onProgress(mMp.getCurrentPosition());
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mPlayerListener != null) {
            mPlayerListener.end();
        }
    }
}

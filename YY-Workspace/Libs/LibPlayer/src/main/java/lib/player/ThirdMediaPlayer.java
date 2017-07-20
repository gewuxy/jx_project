package lib.player;

import android.content.Context;

import com.pili.pldroid.player.PLMediaPlayer;

import java.io.IOException;

/**
 * @auther : GuoXuan
 * @since : 2017/7/7
 */
public class ThirdMediaPlayer implements PLMediaPlayer.OnPreparedListener, PLMediaPlayer.OnCompletionListener, PLMediaPlayer.OnErrorListener {

    private PLMediaPlayer mMediaPlayer;
    private OnPlayerListener mListener;

    public interface OnPlayerListener {
        void onPrepared();
        boolean onError(int i);
        void onCompletion();
    }

    public ThirdMediaPlayer(Context context) {
        mMediaPlayer = new PLMediaPlayer(context);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void setPlayerListener(OnPlayerListener listener) {
        mListener = listener;
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void seekTo(int mSec) {
        mMediaPlayer.seekTo(mSec);
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public void release() {
        mMediaPlayer.release();
    }

    public void reset() {
        mMediaPlayer.reset();
    }

    public long getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    public long getDuration(){
        return mMediaPlayer.getDuration();
    }

    public void setDataSource(String path) throws IOException {
        mMediaPlayer.setDataSource(path);
    }

    public void prepareAsync() {
        mMediaPlayer.prepareAsync();
    }

    public void isPlaying() {
        mMediaPlayer.isPlaying();
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        if (mListener != null) {
            mListener.onPrepared();
        }
    }

    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
        if (mListener != null) {
            return mListener.onError(i);
        }
        return false;
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        if (mListener != null) {
            mListener.onCompletion();
        }
    }
}

package yy.doctor.util;

import android.media.MediaPlayer;
import android.support.annotation.IntDef;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.model.MapList;
import lib.ys.ui.interfaces.impl.NetworkOpt;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.network.NetworkAPISetter.CommonAPI;

/**
 * 自带下载功能的播放器(音频和视频的多媒体暂时不一样)
 *
 * @auther : GuoXuan
 * @since : 2017/9/5
 */
public class NetPlayer implements
        OnCountDownListener,
        OnNetworkListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        PLMediaPlayer.OnPreparedListener,
        PLMediaPlayer.OnCompletionListener {

    private static final String TAG = NetPlayer.class.getSimpleName().toString();

    private static final int KTime = 3; // 默认数三秒
    public static final int KMaxProgress = 100;

    private static NetPlayer mPlayer;

    @IntDef({
            PlayType.audio,
            PlayType.video,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayType {
        int audio = 0;
        int video = 1;
    }

    private CountDown mCountDown; // 计时器
    private MapList<Integer, String> mPaths; // 记录文件路径
    private int mPlayCode; // 选中的
    private OnPlayerListener mListener;

    private MediaPlayer mAudioPlay; // 音频
    private PLVideoTextureView mVideoPlay;// 视频
    private long mAllTime;

    @PlayType
    private int mType; // 播放类型
    private boolean mAutoPlay; // 准备完了是否播放

    private String mMeetId;
    private int mProgress; // 进度

    public interface OnPlayerListener {

        /**
         * 下载的百分比
         *
         * @param progress
         */
        void onDownProgress(int progress);

        /**
         * 准备成功
         *
         * @param allMilliseconds 音频总时长 (毫秒)
         */
        void onPreparedSuccess(long allMilliseconds);

        void onPreparedError();

        /**
         * 播放进度(100% 不一定会回调)
         *
         * @param currMilliseconds 当前播放时间 (毫秒)
         * @param progress         百分比
         */
        void onProgress(long currMilliseconds, int progress);

        /**
         * 播放状态
         *
         * @param state true 播放 false 暂停
         */
        void onPlayState(boolean state);

        /**
         * 播放结束
         */
        void onCompletion();

    }

    public synchronized static NetPlayer inst() {
        if (mPlayer == null) {
            mPlayer = new NetPlayer();
        }
        return mPlayer;
    }

    public void setVideo(PLVideoTextureView textureView) {
        mType = PlayType.video;
        mVideoPlay = textureView;
        mVideoPlay.setOnPreparedListener(this);
        mVideoPlay.setOnCompletionListener(this);
    }

    public void setAudio() {
        mType = PlayType.audio;
    }

    public void setListener(OnPlayerListener listener) {
        mListener = listener;
    }

    private NetPlayer() {
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mPaths = new MapList<>();
        mAudioPlay = new MediaPlayer();
        mAudioPlay.setOnPreparedListener(this);
        mAudioPlay.setOnCompletionListener(this);
        mProgress = ConstantsEx.KInvalidValue;
    }

    @Override
    public void onCountDown(long remainCount) {
        YSLog.d(TAG, "onCountDown:" + remainCount);
        if (remainCount == 0) {
            // 倒数结束
            mCountDown.start(KTime);
            YSLog.d(TAG, "onCountDown:重新开始");
        }
        if (mType == PlayType.audio) {
            if (mAudioPlay.isPlaying() && mListener != null) {
                int cur = mAudioPlay.getCurrentPosition();
                mListener.onProgress(cur, (int) (cur * KMaxProgress / mAllTime));
            }
        } else {
            if (mVideoPlay.isPlaying() && mListener != null) {
                long cur = mVideoPlay.getCurrentPosition();
                mListener.onProgress(cur, (int) (cur * KMaxProgress / mAllTime));
            }
        }
    }

    @Override
    public void onCountDownErr() {
        // do nothing
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mAllTime = mAudioPlay.getDuration();

        if (mAutoPlay) {
            nativePlay();
        }
        if (mListener != null) {
            mListener.onPreparedSuccess(mAllTime);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mAutoPlay = false;
        preparePlay(mPaths.getByKey(mPlayCode));
        mCountDown.stop();
        if (mListener != null) {
            mListener.onCompletion();
        }
        if (mListener != null) {
            mListener.onProgress(mAllTime, KMaxProgress);
        }
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        mAllTime = mVideoPlay.getDuration();

        if (mAutoPlay) {
            nativePlay();
        }
        if (mListener != null) {
            mListener.onPreparedSuccess(mAllTime);
        }

    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        mAutoPlay = false;
        preparePlay(mPaths.getByKey(mPlayCode));
        mCountDown.stop();
        if (mListener != null) {
            mListener.onCompletion();
        }
        if (mListener != null) {
            mListener.onProgress(mAllTime, KMaxProgress);
        }
    }

    /**
     * 播放准备(准备完成自动播放)
     *
     * @param meetId 会议Id
     * @param url    播放Url
     */
    public void prepare(String meetId, String url) {
        prepare(meetId, url, true);
    }

    /**
     * 播放准备
     *
     * @param meetId 会议Id
     * @param url    播放Url
     */
    public void prepare(String meetId, String url, boolean play) {
        if (TextUtil.isEmpty(meetId) || TextUtil.isEmpty(url)) {
            return;
        }

        if (TextUtil.isNotEmpty(mMeetId)) {
            // 同一个会议只要赋值一次(NetPlayer只会存在一个会议中)
            mMeetId = meetId;
        }

        int code = url.hashCode();
        if (mPlayCode == code) {
            // 不用重复准备
            return;
        }
        mPlayCode = code;

        mAutoPlay = play;

        String filePath = CacheUtil.getMeetingCacheDir(meetId); // 文件夹名字 (meetId)

        // 转化文件名
        String type = url.substring(url.lastIndexOf(".") + 1); // 文件类型
        String fileName = String.valueOf(mPlayCode).concat(".").concat(type); // 转化后的文件名

        // 全路径 (文件路径)
        String pathLocal = filePath.concat(fileName);

        // (读内存)
        String pathMemory = mPaths.getByKey(Integer.valueOf(mPlayCode));
        if (TextUtil.isNotEmpty(pathMemory)) {
            // 之前已经下载过
            preparePlay(pathMemory);
            YSLog.d(TAG, "prepare:Memory=" + pathLocal);
            return;
        }

        // 读文件是否存在
        File file = new File(pathLocal);
        if (file.exists()) {
            // 存在 准备播放(读磁盘)
            preparePlay(pathLocal);
            YSLog.d(TAG, "prepare:Local=" + pathLocal);
        } else {
            // 不存在 下载(读网络)
            exeNetworkReq(mPlayCode, CommonAPI.download(filePath, fileName, url).build());
            YSLog.d(TAG, "prepare:Net=" + pathLocal);
        }

        // 添加到内存中
        mPaths.add(Integer.valueOf(mPlayCode), pathLocal); // 不等下载成功添加(多个同时成功)
    }

    /**
     * 资源准备
     *
     * @param path 播放文件路径
     */
    private void preparePlay(String path) {
        if (mType == PlayType.audio) {
            try {
                mAudioPlay.reset();
                mAudioPlay.setDataSource(path);
                mAudioPlay.prepareAsync();

                YSLog.d(TAG, "preparePlay:" + path);
            } catch (Exception e) {
                if (mListener != null) {
                    mListener.onPreparedError();
                }

                YSLog.e(TAG, "preparePlay:", e);
            }
        } else {
            AVOptions options = new AVOptions();
            options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
            mVideoPlay.setAVOptions(options);
            mVideoPlay.setVideoPath(path);
        }
    }

    /**
     * 内部播放
     */
    private void nativePlay() {
        if (mProgress != ConstantsEx.KInvalidValue) {
            seekTo((int) (mProgress * mAllTime / NetPlayer.KMaxProgress));
            mProgress = ConstantsEx.KInvalidValue;
        }
        mCountDown.start(KTime);
        if (mType == PlayType.audio) {
            mAudioPlay.start();
        } else {
            mVideoPlay.start();
        }
        if (mListener != null) {
            mListener.onPlayState(true);
        }
    }

    /**
     * 播放
     */
    public void play(String url) {
        if (TextUtil.isEmpty(url)) {
            return;
        }
        int code = url.hashCode();
        if (code == mPlayCode) {
            nativePlay();
        } else {
            prepare(mMeetId, url);
        }
    }

    /**
     * 播放
     */
    public void play(String url, int progress) {
        mProgress = progress;
        play(url);
    }

    /**
     * 暂停
     */
    public void pause() {
        mAutoPlay = false;
        mCountDown.stop();
        if (mType == PlayType.audio) {
            if (mAudioPlay.isPlaying()) {
                mAudioPlay.pause();
            }
        } else {
            if (mVideoPlay.isPlaying()) {
                mVideoPlay.pause();
            }
        }
        if (mListener != null) {
            mListener.onPlayState(false);
        }
    }

    public void toggle(String url) {
        if (mAudioPlay.isPlaying() || (mVideoPlay != null && mVideoPlay.isPlaying())) {
            pause();
        } else {
            play(url);
        }
    }

    public void toggle(String url, int progress) {
        mProgress = progress;
        toggle(url);
    }

    /**
     * 拖拽
     *
     * @param msec 毫秒
     */
    public void seekTo(int msec) {
        if (msec < 0 || msec > mAllTime) {
            return;
        }
        mCountDown.start(KTime);
        if (mType == PlayType.audio) {
            mAudioPlay.seekTo(msec);
        } else {
            mVideoPlay.seekTo(msec);
        }
    }

    /**
     * 停止
     */
    public void stop() {
        mAutoPlay = false;
        mCountDown.stop();
        if (mType == PlayType.audio) {
            if (mAudioPlay.isPlaying()) {
                mAudioPlay.stop();
            }
        } else {
            if (mVideoPlay.isPlaying()) {
                mVideoPlay.stopPlayback();
            }
        }
        if (mListener != null) {
            mListener.onPlayState(false);
        }
    }

    /**
     * 取消监听
     */
    public void removeListener() {
        mListener = null;
    }

    /**
     * 释放资源, 统一置空
     */
    public void recycle() {
        mPaths.clear();
        mPaths = null;

        if (mAudioPlay.isPlaying()) {
            mAudioPlay.stop();
        }
        mAudioPlay.release();
        mAudioPlay = null;

        if (mVideoPlay != null && mVideoPlay.isPlaying()) {
            mVideoPlay.stopPlayback();
        }

        mCountDown.recycle();
        mCountDown = null;

        if (mNetworkImpl != null) {
            mNetworkImpl.onDestroy();
            mNetworkImpl = null;
        }

        mListener = null;
        mPlayer = null;
    }

    /**********
     ********** 网络相关
     **********/

    private NetworkOpt mNetworkImpl;

    private void exeNetworkReq(int id, NetworkReq req) {
        if (mNetworkImpl == null) {
            mNetworkImpl = new NetworkOpt(this, this);
        }
        mNetworkImpl.exeNetworkReq(id, req, this);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        YSLog.d(TAG, "onNetworkSuccess:" + id);
        if (id == mPlayCode) {
            // 准备当前选中
            preparePlay(mPaths.getByKey(id));
            if (mListener != null) {
                mListener.onDownProgress(KMaxProgress);
            }
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        YSLog.d(TAG, "onNetworkError:remove=" + id);
        // 删除 文件 和 内存记录
        String pathLocal = mPaths.getByKey(id);
        File file = new File(pathLocal);
        if (file.exists()) {
            FileUtil.delFile(file);
        }
        mPaths.removeByKey(Integer.valueOf(id));

        if (id == mPlayCode && mListener != null) {
            mListener.onPreparedError();
        }
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        if (id == mPlayCode && mListener != null) {
            mListener.onDownProgress((int) progress);
        }
    }
}

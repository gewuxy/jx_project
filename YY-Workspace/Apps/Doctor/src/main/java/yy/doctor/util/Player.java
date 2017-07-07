package yy.doctor.util;

import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLMediaPlayer.OnCompletionListener;
import com.pili.pldroid.player.PLMediaPlayer.OnPreparedListener;

import java.io.File;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.AppEx;
import lib.ys.YSLog;
import lib.ys.model.MapList;
import lib.ys.ui.interfaces.impl.NetworkOpt;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.App;
import yy.doctor.network.NetFactory;

/**
 * 单例播放器
 *
 * @auther : GuoXuan
 * @since : 2017/6/24
 */

public class Player implements
        OnCountDownListener,
        OnNetworkListener,
        OnCompletionListener,
        OnPreparedListener {

    private static final String TAG = Player.class.getSimpleName().toString();
    private static final int KTime = 3; // 默认数三秒

    private static Player mPlayer;

    private PLMediaPlayer mMp;
    private CountDown mCountDown; // 计时
    private OnPlayerListener mPlayerListener;
    private MapList<Integer, String> mFiles;
    private int mCode;

    public interface OnPlayerListener {
        /**
         * 播放进度(100% 不一定会回调)
         *
         * @param currMilliseconds 当前播放时间 (毫秒)
         * @param allMilliseconds  音频总时长 (毫秒)
         * @param progress         百分比
         */
        void onProgress(long currMilliseconds, long allMilliseconds, int progress);

        /**
         * 播放结束
         */
        void end();
    }

    public synchronized static Player inst() {
        if (mPlayer == null) {
            mPlayer = new Player();
        }
        return mPlayer;
    }

    public void setPlayerListener(OnPlayerListener playerListener) {
        mPlayerListener = playerListener;
    }

    private Player() {
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mMp = new PLMediaPlayer(App.getContext());
        mMp.setOnPreparedListener(this);
        mMp.setOnCompletionListener(this);
        mFiles = new MapList<>();
    }

    /**
     * @param meetId   会议的id
     * @param audioUrl 音频的Url
     */
    public void prepare(String meetId, String audioUrl) {
        // 文件名
        String type = audioUrl.substring(audioUrl.lastIndexOf(".") + 1);
        String fileName = audioUrl.hashCode() + "." + type;
        // 文件夹名字 (meetId)
        String filePath = CacheUtil.getMeetingCacheDir(meetId);
        // 全路径 (音频文件路径)
        String audioFilePath = filePath + fileName;
        // 检查文件是否存在
        File file = CacheUtil.getMeetingCacheFile(meetId, fileName);
        mCode = audioFilePath.hashCode();
        mFiles.add(Integer.valueOf(mCode), audioFilePath);
        if (!file.exists()) {
            // 不存在 下载
            YSLog.d(TAG, "prepare:" + audioUrl);
            exeNetworkReq(mCode, NetFactory.newDownload(audioUrl, filePath, fileName).build());
        } else {
            // 存在 准备播放
            preparePlay(audioFilePath);
        }

    }

    /**
     * 准备播放
     */
    private void preparePlay(String path) {
        try {
            mMp.reset();
            mMp.setDataSource(path);
            mMp.prepareAsync();

            YSLog.d(TAG, "preparePlay:" + path);
        } catch (Exception e) {
            YSLog.e(TAG, "start", e);
        }
    }

    /**
     * 播放
     */
    public void play() {
        mMp.start();
        mCountDown.start(KTime);
    }

    /**
     * 拖拽
     *
     * @param msec 毫秒
     */
    public void seekTo(int msec) {
        mMp.seekTo(msec);
        mCountDown.start(KTime);
    }

    /**
     * 暂停
     */
    public void pause() {
        mMp.pause();
        mCountDown.stop();
    }

    /**
     * 停止
     */
    public void stop() {
        mMp.stop();
        mCountDown.stop();
    }

    /**
     * 释放资源, 统一置空
     */
    public void recycle() {
        mMp.release();
        mMp = null;
        mPlayer = null;
        mCountDown.recycle();
        mCountDown = null;
    }


    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {

    }

    @Override
    public void onCountDown(long remainCount) {
        long curr = mMp.getCurrentPosition();
        long all = mMp.getDuration();

        if (mPlayerListener != null) {
            mPlayerListener.onProgress(curr, all, (int) (curr * 100 / all));
        }

        if (remainCount == 0 && curr < all) {
            // 计时结束, 音频没播放完重新开始计时
            mCountDown.start(KTime);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        mCountDown.stop();
        if (mPlayerListener != null) {
            mPlayerListener.end();
        }
    }

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
        YSLog.d(TAG, "onNetworkSuccess:" + mFiles.getByKey(id));
        if (id == mCode) {
            preparePlay(mFiles.getByKey(id));
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        AppEx.showToast(error.getMessage());
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
    }
}

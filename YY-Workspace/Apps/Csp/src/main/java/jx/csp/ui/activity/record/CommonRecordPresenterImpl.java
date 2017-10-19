package jx.csp.ui.activity.record;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import jx.csp.R;
import jx.csp.ui.activity.record.CommonRecordContract.CommonRecordPresenter;
import jx.csp.ui.activity.record.CommonRecordContract.CommonRecordView;
import jx.csp.ui.activity.record.RecordImgFrag.onMediaPlayerListener;
import jx.csp.util.Util;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public class CommonRecordPresenterImpl implements CommonRecordPresenter, onMediaPlayerListener, OnCountDownListener {

    private final int KSixtySecond = 60;
    private CommonRecordView mRecordView;
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;

    private boolean mPlayState = false; // 是否在播放
    private long mTotalTime = 0; // 录制的总共时间 单位秒
    private long mTime; // 录制的时间 单位秒
    private CountDown mCountDown;

    public CommonRecordPresenterImpl(CommonRecordView recordView) {
        mRecordView = recordView;
        mMediaRecorder = new MediaRecorder();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void startRecord(String filePath) {
        mTime = 0;
        if (mCountDown == null) {
            mCountDown = new CountDown();
            mCountDown.setListener(this);
        }
        File file = new File(filePath);
        mMediaRecorder.reset();
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(OutputFormat.DEFAULT);
        mMediaRecorder.setAudioEncoder(AudioEncoder.AAC);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mCountDown.start(KSixtySecond);
            mRecordView.startRecordState();
        } catch (IOException e) {
            mRecordView.showToast(R.string.record_fail);
        }
    }

    @Override
    public void stopRecord() {
        mTotalTime  += mTime;
        mRecordView.setTotalRecordTimeTv(Util.getSpecialTimeFormat(mTotalTime));
        mRecordView.stopRecordState();
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onDestroy() {
        mMediaRecorder = null;
        mMediaPlayer = null;
        if (mCountDown != null) {
            mCountDown.recycle();
        }
    }

    @Override
    public void startPlay(String filePath, RecordImgFrag frag) {
        File soundFile = new File(filePath);
        if (!soundFile.exists()) {
            return;
        }
        if (mCountDown == null) {
            mCountDown = new CountDown();
            mCountDown.setListener(this);
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(soundFile.getAbsolutePath());
            //同步准备
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mCountDown.start(KSixtySecond);
            mMediaPlayer.setOnCompletionListener(mp -> {
                frag.stopAnimation();
                stopPlay();
            });
            mPlayState = true;
        } catch (IOException e) {
            mRecordView.showToast(R.string.play_fail);
        }
    }

    @Override
    public void stopPlay() {
        if (mMediaPlayer != null) {
            if (mPlayState) {
                mMediaPlayer.stop();
                mRecordView.goneViceLine();
            }
            mMediaPlayer.reset();
        }
        if (mCountDown != null) {
            mCountDown.stop();
        }
        mPlayState = false;
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            mCountDown.start(KSixtySecond);
        }
        // 录制跟播放有不同的操作
        if (mPlayState) {
            int i = new Random().nextInt(20) + 5;
            mRecordView.setVoiceLineState(i);
        } else {
            ++mTime;
            mRecordView.setRecordTimeTv(Util.getSpecialTimeFormat(mTime));
        }
    }

    @Override
    public void onCountDownErr() {
    }
}
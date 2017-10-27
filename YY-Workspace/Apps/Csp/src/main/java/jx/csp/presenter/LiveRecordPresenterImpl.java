package jx.csp.presenter;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jx.csp.App;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.contact.LiveRecordContract;
import jx.csp.contact.LiveRecordContract.View;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import lib.zego.IZegoCallback;
import lib.zego.IZegoCallback.UserType;
import lib.zego.ZegoApiManager;

/**
 * @author CaiXiang
 * @since 2017/10/11
 */

public class LiveRecordPresenterImpl implements LiveRecordContract.Presenter, OnCountDownListener {

    private static final String TAG = "LiveRecordPresenterImpl";
    private final int KFifteen = 15; // 开始倒计时的分钟数
    private View mLiveRecordView;
    private MediaRecorder mMediaRecorder;
    private CountDown mCountDown;
    private long mStartTime;
    private long mStopTime;
    private boolean mShowCountDownRemainTv = false; // 倒计时的Tv是否显示

    private ZegoCallbackImpl mZegoCallbackImpl;

    public LiveRecordPresenterImpl(View view) {

        mLiveRecordView = view;
        mMediaRecorder = new MediaRecorder();
        mZegoCallbackImpl = new ZegoCallbackImpl();
        ZegoApiManager.getInstance().init(App.getContext(),"666", "人数获取测试");
        //测试
        ZegoApiManager.getInstance().setTestEnv(BuildConfig.TEST);
        ZegoApiManager.getInstance().setRoomConfig(true, true);
        ZegoApiManager.getInstance().loginRoom("789", UserType.audience, mZegoCallbackImpl);
        ZegoApiManager.getInstance().setZegoIMCallback(mZegoCallbackImpl);
    }

    @Override
    public void startCountDown(long startTime, long stopTime) {
        mStartTime = startTime;
        mStopTime = stopTime;
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mCountDown.start((mStopTime - System.currentTimeMillis()) / TimeUnit.SECONDS.toMillis(1));
    }

    @Override
    public void startLiveRecord(String filePath) {
        File file = new File(filePath);
        mMediaRecorder.reset();
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(OutputFormat.AMR_WB);
        mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_WB);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            YSLog.d(TAG, "startLiveRecord time = " + System.currentTimeMillis());
            mLiveRecordView.startRecordState();
        } catch (IOException e) {
            mLiveRecordView.showToast(R.string.record_fail);
        }
    }

    @Override
    public void stopLiveRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            YSLog.d(TAG, "stopLiveRecord time = " + System.currentTimeMillis());
        }
        mLiveRecordView.stopRecordState();
    }

    @Override
    public void onDestroy() {
        mMediaRecorder = null;
        if (mCountDown != null) {
            mCountDown.recycle();
        }
        ZegoApiManager.getInstance().logoutRoom();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            mLiveRecordView.onFinish();
        }
        long time = (mStopTime - mStartTime) / 1000 - remainCount;
        mLiveRecordView.setLiveTimeTv(Util.getSpecialTimeFormat(time));
        if (remainCount <= TimeUnit.MINUTES.toSeconds(KFifteen)) {
            if (!mShowCountDownRemainTv) {
                mShowCountDownRemainTv = !mShowCountDownRemainTv;
                mLiveRecordView.changeRecordIvRes();
            }
            mLiveRecordView.setCountDownRemainTv(mShowCountDownRemainTv, remainCount);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    private class ZegoCallbackImpl extends IZegoCallback {

        @Override
        public void onLoginCompletion(int i, String stream) {
            // i   0:成功, 其它:失败
            YSLog.d(TAG, "i" + i);
        }

        @Override
        public void onUserUpdate(int number) {
            mLiveRecordView.setOnlineTv(String.valueOf(number));
        }
    }
}

package jx.csp.presenter;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jx.csp.App;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.contact.LiveRecordContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.ui.frag.record.RecordImgFrag.AudioType;
import jx.csp.util.Util;
import lib.live.ILiveCallback;
import lib.live.ILiveCallback.UserType;
import lib.live.LiveApi;
import lib.ys.YSLog;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * @author CaiXiang
 * @since 2017/10/11
 */

public class LiveRecordPresenterImpl extends BasePresenterImpl<LiveRecordContract.V> implements LiveRecordContract.P, OnCountDownListener {

    private final String TAG = getClass().getSimpleName();
    private final int KFifteen = 15; // 开始倒计时的分钟数
    private final int KMsgWhat = 10;
    private MediaRecorder mMediaRecorder;
    private CountDown mCountDown;
    private long mStartTime;
    private long mStopTime;
    private boolean mShowCountDownRemainTv = false; // 倒计时的Tv是否显示
    private boolean mOverFifteen = false;  // 是否在录制超过15分钟的音频
    private int mNum = 0;

    private Message mMsg;
    private String mFilePath;
    private LiveCallbackImpl mLiveCallbackImpl;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == KMsgWhat) {
                YSLog.d(TAG, "收到15m倒计时结束指令");
                // 先暂停录音，然后再开始录音，上传这15m的音频
                mOverFifteen = true;
                stopLiveRecord();
                getView().upload(PlayType.live, mFilePath);
                // 截取文件路径不包括后缀
                String str;
                if (mNum > 0) {
                    str = (String) mFilePath.subSequence(0, mFilePath.lastIndexOf("-"));
                } else {
                    str = (String) mFilePath.subSequence(0, mFilePath.lastIndexOf("."));
                }
                YSLog.d(TAG, "str = " + str);
                mNum++;
                // 重新命名下一段音频文件名 格式3-1   3-2   3-3
                String newFilePath = str + "-" + mNum + "." + AudioType.amr;
                YSLog.d(TAG, "newFilePath = " + newFilePath);
                startLiveRecord(newFilePath);
                mHandler.sendEmptyMessageDelayed(KMsgWhat, TimeUnit.MINUTES.toMillis(KFifteen));
            }
        }
    };

    public LiveRecordPresenterImpl(LiveRecordContract.V view) {
        super(view);

        mMediaRecorder = new MediaRecorder();
        mLiveCallbackImpl = new LiveCallbackImpl();
        LiveApi.getInst().init(App.getContext(), Profile.inst().getString(TProfile.id) + "666", Profile.inst().getString(TProfile.userName));
        //测试
        LiveApi.getInst().setTest(BuildConfig.TEST);
        LiveApi.getInst().setRoomConfig(true, true);
        LiveApi.getInst().setCallback("789", UserType.audience, mLiveCallbackImpl);
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
        if (!filePath.contains("-")) {
            YSLog.d(TAG, "不包含-");
            mOverFifteen = false;
            mNum = 0;
        }
        mFilePath = filePath;
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
            getView().setAudioFilePath(mFilePath);
            getView().startRecordState();
            // 直播的时候每页只能录音15分钟，到15分钟的时候要要先上传这15分钟的音频
            mHandler.sendEmptyMessageDelayed(KMsgWhat, TimeUnit.MINUTES.toMillis(KFifteen));
            getView().startRecordState();
        } catch (IOException e) {
            getView().showToast(R.string.record_fail);
        }
    }

    @Override
    public void stopLiveRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            YSLog.d(TAG, "stopLiveRecord time = " + System.currentTimeMillis());
        }
        getView().stopRecordState();
        mHandler.removeMessages(KMsgWhat);
        if (!mOverFifteen) {
            mNum = 0;
        }
        getView().stopRecordState();
    }

    @Override
    public void onDestroy() {
        mMediaRecorder = null;
        if (mCountDown != null) {
            mCountDown.recycle();
        }
        mHandler.removeCallbacksAndMessages(null);
        LiveApi.getInst().logoutRoom();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            getView().onFinish();
        }
        long time = (mStopTime - mStartTime) / 1000 - remainCount;
        getView().setLiveTimeTv(Util.getSpecialTimeFormat(time, "'", "''"));
        if (remainCount <= TimeUnit.MINUTES.toSeconds(KFifteen)) {
            if (!mShowCountDownRemainTv) {
                mShowCountDownRemainTv = !mShowCountDownRemainTv;
                getView().changeRecordIvRes();
            }
            getView().setCountDownRemainTv(mShowCountDownRemainTv, remainCount);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    private class LiveCallbackImpl extends ILiveCallback {

        @Override
        public void onLoginCompletion(int i, String stream) {
            // i   0:成功, 其它:失败
            YSLog.d(TAG, "i" + i);
        }

        @Override
        public void onUserUpdate(int number) {
            getView().setOnlineTv(String.valueOf(number));
        }
    }
}

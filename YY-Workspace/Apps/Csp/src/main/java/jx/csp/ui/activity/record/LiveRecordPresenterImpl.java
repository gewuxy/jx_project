package jx.csp.ui.activity.record;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.constants.ZegoConstants.RoomRole;
import com.zego.zegoliveroom.entity.ZegoConversationMessage;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoUserState;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.ui.activity.record.LiveRecordContract.LiveRecordPresenter;
import jx.csp.ui.activity.record.LiveRecordContract.LiveRecordView;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import lib.zego.ZegoApiManager;

/**
 * @author CaiXiang
 * @since 2017/10/11
 */

public class LiveRecordPresenterImpl implements LiveRecordPresenter, OnCountDownListener {

    private static final String TAG = "LiveRecordPresenterImpl";
    private final int KFifteen = 15; // 开始倒计时的分钟数
    private LiveRecordView mLiveRecordView;
    private MediaRecorder mMediaRecorder;
    private CountDown mCountDown;
    private long mStartTime;
    private long mStopTime;
    private boolean mShowCountDownRemainTv = false; // 倒计时的Tv是否显示
    private ZegoLiveRoom mZegoLiveRoom;

    public LiveRecordPresenterImpl(LiveRecordView view) {
        mLiveRecordView = view;
        mMediaRecorder = new MediaRecorder();

        mZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom();
        //测试
        mZegoLiveRoom.setTestEnv(BuildConfig.TEST);
        mZegoLiveRoom.setRoomConfig(true, true);
        mZegoLiveRoom.loginRoom("789", RoomRole.Audience, new IZegoLoginCompletionCallback() {

            @Override
            public void onLoginCompletion(int i, ZegoStreamInfo[] zegoStreamInfos) {
                YSLog.d(TAG, " onLoginCompletion i = " + i);
            }
        });
        mZegoLiveRoom.setZegoIMCallback(new IZegoIMCallback() {

            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {
                //直播间的观众人数获取
                YSLog.d(TAG, "user state = " + zegoUserStates.length);
                mLiveRecordView.setOnlineTv(String.valueOf(zegoUserStates.length));
            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {
            }

            @Override
            public void onRecvConversationMessage(String s, String s1, ZegoConversationMessage zegoConversationMessage) {
            }
        });
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
        mMediaRecorder.setOutputFormat(OutputFormat.DEFAULT);
        mMediaRecorder.setAudioEncoder(AudioEncoder.AAC);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
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
        }
        mLiveRecordView.stopRecordState();
    }

    @Override
    public void onDestroy() {
        mMediaRecorder = null;
        if (mCountDown != null) {
            mCountDown.recycle();
        }
        mZegoLiveRoom.setZegoIMCallback(null);
        mZegoLiveRoom.logoutRoom();
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
}

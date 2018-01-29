package jx.csp.presenter;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jx.csp.R;
import jx.csp.constant.AudioType;
import jx.csp.contact.LiveAudioContract;
import jx.csp.contact.LiveAudioContract.V;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.util.Util;
import lib.jx.contract.BasePresenterImpl;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;

/**
 * @author CaiXiang
 * @since 2017/10/11
 */

public class LiveAudioPresenterImpl extends BasePresenterImpl<V> implements
        LiveAudioContract.P,
        OnCountDownListener {

    private final String TAG = getClass().getSimpleName();
    private final int KJoinMeetingReqId = 1;
    private final int KStartLiveReqId = 2;  // 开始直播
    private final int KUploadVideoPage = 3;  // 视频页翻页时调用的
    private final int KAudioMaxRecordTime = (int) TimeUnit.MINUTES.toMillis(10); // 每页ppt最长的录制时长
    private final int KRecordMsgWhat = 1;
    private final int KSendSyncMsgWhat = 2; // 发送同步指令
    private final int KStartLiveMsgWhat = 3; // 点击开始直播的时候延时3秒请求服务器发送同步指令
    private MediaRecorder mMediaRecorder;
    private CountDown mCountDown;
    private boolean mOverFifteen = false;  // 是否在录制超过15分钟的音频
    private int mNum = 0;
    private String mFilePath;
    private int mWsPosition = -1;  // websocket接收到的页数
    private int mTime = 0; // 每页ppt录制的时间 单位秒
    private boolean mLastSyncOrderState = false;  // 上一页的同步指令是否已经发送
    private boolean mLiveState = false;  // 是否在直播
    private boolean mRecordState = false;  // 是否录音

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KRecordMsgWhat: {
                    YSLog.d(TAG, "收到15m倒计时结束指令");
                    // 先暂停录音，然后再开始录音，上传这15m的音频
                    mOverFifteen = true;
                    stopLiveRecord(false);
                    getView().joinUploadRank(mFilePath, (int) TimeUnit.MINUTES.toSeconds(10));
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
                    startLiveRecord(newFilePath, false);
                    mHandler.sendEmptyMessageDelayed(KRecordMsgWhat, KAudioMaxRecordTime);
                }
                break;
                case KSendSyncMsgWhat: {
                    // 直播的时候翻页，在页面停留时间小于3秒不发同步指令，超过3秒才发同步指令
                    // 如果当前页跟网页段发来的是同一页。则不发同步指令
                    int pos = msg.arg1;
                    mLastSyncOrderState = true;
                    YSLog.d(TAG, "翻页后收到延时3秒指令 pos = " + pos);
                    if (mWsPosition != pos) {
                        YSLog.d(TAG, "跟网页端的页数不一样，发同步指令");
                        getView().sendSyncInstruction(pos);
                    } else {
                        YSLog.d(TAG, "跟网页端的页数一样，不发同步指令");
                    }
                }
                break;
                case KStartLiveMsgWhat: {
                    YSLog.d(TAG, "点击开始后，收到延时3秒指令 请求服务器发送同步指令");
                    Bundle bundle = msg.getData();
                    exeNetworkReq(KStartLiveReqId, MeetingAPI.start(bundle.getString("courseId"),
                            bundle.getString("videoUrl"),
                            bundle.getString("imgUrl"),
                            bundle.getInt("firstClk"),
                            bundle.getInt("pageNum")).build());
                }
                break;
            }
        }
    };

    public LiveAudioPresenterImpl(LiveAudioContract.V view) {
        super(view);

        mMediaRecorder = new MediaRecorder();
        mCountDown = new CountDown();
        mCountDown.setListener(this);
    }

    @Override
    public void getData(String courseId) {
        exeNetworkReq(KJoinMeetingReqId, MeetingAPI.join(courseId).build());
    }

    @Override
    public void startLive(String courseId, String videoUrl, String imgUrl, int firstClk, int pageNum) {
        // 先去除mHandler的对应消息，再延时发送消息
        mHandler.removeMessages(KStartLiveMsgWhat);
        Message msg = new Message();
        msg.what = KStartLiveMsgWhat;
        Bundle bundle = new Bundle();
        bundle.putString("courseId", courseId);
        bundle.putString("videoUrl", videoUrl);
        bundle.putString("imgUrl", imgUrl);
        bundle.putInt("firstClk", firstClk);
        bundle.putInt("pageNum", pageNum);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, TimeUnit.SECONDS.toMillis(3));
    }

    @Override
    public void setLiveState(boolean state) {
        mLiveState = state;
    }

    @Override
    public void changePage(int pos) {
        // 先去除mHandler的对应消息，再延时发送消息
        mLastSyncOrderState = false;
        mHandler.removeMessages(KSendSyncMsgWhat);
        Message msg = new Message();
        msg.what = KSendSyncMsgWhat;
        msg.arg1 = pos;
        mHandler.sendMessageDelayed(msg, TimeUnit.SECONDS.toMillis(3));
    }

    @Override
    public void setWsPos(int pos) {
        mWsPosition = pos;
    }

    @Override
    public void startLiveRecord(String filePath, boolean changeState) {
        mHandler.removeMessages(KRecordMsgWhat);
        Util.runOnSubThread(() -> {
            if (!filePath.contains("-")) {
                YSLog.d(TAG, "不包含“-”, 上一段录制时间没有超过15分钟");
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
                if (!mRecordState) {
                    mMediaRecorder.start();
                    mRecordState = true;
                }
                mTime = 0;
                YSLog.d(TAG, "startRecord time = " + System.currentTimeMillis());
                Util.runOnUIThread(() -> {
                    getView().setAudioFilePath(mFilePath);
                    if (changeState) {
                        getView().startRecordState();
                    }
                    // 直播的时候每页只能录音10分钟，到10分钟的时候要要先上传这15分钟的音频
                    mHandler.sendEmptyMessageDelayed(KRecordMsgWhat, KAudioMaxRecordTime);
                });
            } catch (IOException e) {
                Util.runOnUIThread(() -> getView().showToast(R.string.record_fail));
            }
        });
    }

    @Override
    public void stopLiveRecord(boolean changeState) {
        mHandler.removeMessages(KStartLiveMsgWhat);
        mHandler.removeMessages(KRecordMsgWhat);
        getView().setRecordTime(mTime);
        Util.runOnSubThread(() -> {
            if (mMediaRecorder != null && mRecordState) {
                mMediaRecorder.stop();
                mRecordState = false;
                YSLog.d(TAG, "stopRecord time = " + System.currentTimeMillis());
            }
            if (!mOverFifteen) {
                mNum = 0;
            }
        });
        if (changeState) {
            getView().stopRecordState();
        }
    }

    @Override
    public void uploadVideoPage(String courseId, String courseDetailId) {
        if (mLastSyncOrderState) {
            YSLog.d(TAG, "上页是视频 停留时间超过3秒 请求服务器发送直播指令");
            exeNetworkReq(KUploadVideoPage, MeetingAPI.videoNext(courseId, courseDetailId).build());
        } else {
            YSLog.d(TAG, "上页是视频 停留时间没超过3秒 不请求服务器发送直播指令");
        }
    }

    @Override
    public void startCountDown(int time) {
        mTime = 0;
        mCountDown.start(time);
    }

    @Override
    public void stopCountDown() {
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onDestroy() {
        mMediaRecorder = null;
        if (mCountDown != null) {
            mCountDown.recycle();
        }
        mHandler.removeCallbacksAndMessages(null);
        stopCountDown();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (mLiveState) {
            ++mTime;
            getView().setLiveTime();
            if (remainCount <= TimeUnit.MINUTES.toSeconds(5)) {
                if (remainCount > 240 && remainCount <= 300) {
                    getView().setLiveStopRemind(5);
                } else if (remainCount > 180 && remainCount <= 240) {
                    getView().setLiveStopRemind(4);
                } else if (remainCount > 120 && remainCount <= 180) {
                    getView().setLiveStopRemind(3);
                } else if (remainCount > 60 && remainCount <= 120) {
                    getView().setLiveStopRemind(2);
                } else {
                    getView().setLiveStopRemind(1);
                }
            }
        }
        if (remainCount == 0) {
            getView().finishLive(true);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KJoinMeetingReqId) {
            return JsonParser.ev(resp.getText(), JoinMeeting.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KJoinMeetingReqId) {
            if (r.isSucceed()) {
                getView().setData((JoinMeeting) r.getData());
            } else {
                onNetworkError(id, r.getError());
                getView().onStopRefresh();
                getView().finishLive(false);
            }
        } else {
            super.onNetworkSuccess(id, r);
        }
    }
}

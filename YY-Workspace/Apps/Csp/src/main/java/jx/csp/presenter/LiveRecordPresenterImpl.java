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

import jx.csp.R;
import jx.csp.contact.LiveRecordContract;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.frag.record.RecordImgFrag.AudioType;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * @author CaiXiang
 * @since 2017/10/11
 */

public class LiveRecordPresenterImpl extends BasePresenterImpl<LiveRecordContract.V> implements
        LiveRecordContract.P,
        OnCountDownListener {

    private final String TAG = getClass().getSimpleName();
    private final int KJoinMeetingReqId = 1;
    private final int KStartLiveReqId = 2;  // 开始直播
    private final int KUploadVideoPage = 3;  // 视频页翻页时调用的
    private final int KCountDownTime = 15; // 开始倒计时的分钟数
    private final int KRecordMsgWhat = 1;
    private final int KSendSyncMsgWhat = 2; // 发送同步指令
    private MediaRecorder mMediaRecorder;
    private CountDown mCountDown;
    private long mStartTime;
    private long mStopTime;
    private boolean mShowCountDownRemainTv = false; // 倒计时的Tv是否显示
    private boolean mOverFifteen = false;  // 是否在录制超过15分钟的音频
    private int mNum = 0;
    private String mFilePath;
    private int mWsPosition = 0;  // websocket接收到的页数

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == KRecordMsgWhat) {
                YSLog.d(TAG, "收到15m倒计时结束指令");
                // 先暂停录音，然后再开始录音，上传这15m的音频
                mOverFifteen = true;
                stopLiveRecord();
                getView().joinUploadRank(mFilePath);
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
                mHandler.sendEmptyMessageDelayed(KRecordMsgWhat, TimeUnit.MINUTES.toMillis(KCountDownTime));
            } else if (msg.what == KSendSyncMsgWhat) {
                // 直播的时候翻页，在页面停留时间小于3秒不发同步指令，超过3秒才发同步指令
                // 如果当前页跟网页段发来的是同一页。则不发同步指令
                int pos = msg.arg1;
                YSLog.d(TAG, "收到延时3秒指令 pos = " + pos);
                if (mWsPosition != pos) {
                    YSLog.d(TAG, "跟网页端的页数不一样，发同步指令");
                    getView().sendSyncInstruction(pos);
                } else {
                    YSLog.d(TAG, "跟网页端的页数一样，不发同步指令");
                }
            }
        }
    };

    public LiveRecordPresenterImpl(LiveRecordContract.V view) {
        super(view);

        mMediaRecorder = new MediaRecorder();
    }

    @Override
    public void getData(String courseId) {
        exeNetworkReq(KJoinMeetingReqId, MeetingAPI.join(courseId).build());
    }

    @Override
    public void startLive(String courseId, String videoUrl, String imgUrl, int firstClk) {
        exeNetworkReq(KStartLiveReqId, MeetingAPI.start(courseId, videoUrl, imgUrl, firstClk).build());
    }

    @Override
    public void changePage(int pos) {
        // 先去除mHandler的对应消息，再延时发送消息
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
    public void startCountDown(long startTime, long stopTime) {
        mStartTime = startTime;
        mStopTime = stopTime;
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mCountDown.start((mStopTime - System.currentTimeMillis()) / TimeUnit.SECONDS.toMillis(1));
    }

    @Override
    public void startLiveRecord(String filePath) {
        mHandler.removeMessages(KRecordMsgWhat);
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
            mHandler.sendEmptyMessageDelayed(KRecordMsgWhat, TimeUnit.MINUTES.toMillis(KCountDownTime));
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
        mHandler.removeMessages(KRecordMsgWhat);
        if (!mOverFifteen) {
            mNum = 0;
        }
        getView().stopRecordState();
    }

    @Override
    public void uploadVideoPage(String courseId, String courseDetailId) {
        exeNetworkReq(KUploadVideoPage, MeetingAPI.videoNext(courseId, courseDetailId).build());
    }

    @Override
    public void onDestroy() {
        mMediaRecorder = null;
        if (mCountDown != null) {
            mCountDown.recycle();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            getView().finishLive();
        }
        long time = (mStopTime - mStartTime) / 1000 - remainCount;
        getView().setLiveTime(Util.getSpecialTimeFormat(time, "'", "''"));
        if (remainCount <= TimeUnit.MINUTES.toSeconds(KCountDownTime)) {
            if (!mShowCountDownRemainTv) {
                mShowCountDownRemainTv = !mShowCountDownRemainTv;
                getView().changeRecordIvRes();
            }
            getView().setCountDownRemain(mShowCountDownRemainTv, remainCount);
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
            }
        } else {
            super.onNetworkSuccess(id, r);
        }
    }
}

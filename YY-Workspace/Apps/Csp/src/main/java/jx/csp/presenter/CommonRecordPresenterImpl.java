package jx.csp.presenter;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;
import android.util.SparseArray;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import jx.csp.R;
import jx.csp.contact.CommonRecordContract;
import jx.csp.model.meeting.Course.TCourse;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.JoinMeeting.TJoinMeeting;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.frag.record.RecordImgFrag;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.util.CountDown;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public class CommonRecordPresenterImpl extends BasePresenterImpl<CommonRecordContract.View> implements
        CommonRecordContract.Presenter,
        RecordImgFrag.onMediaPlayerListener,
        CountDown.OnCountDownListener {

    private final int KSixtySecond = (int) TimeUnit.MINUTES.toSeconds(1);
    private final int KJoinMeetingReqId = 10;
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;

    private boolean mPlayState = false; // 是否在播放
    private long mTotalTime = 0; // 录制的总共时间 单位秒
    private long mTime; // 录制的时间 单位秒
    private CountDown mCountDown;
    private SparseArray<Integer> mRecordTimeArray;
    private int mPos; // 当前录制的ppt页面下标

    public CommonRecordPresenterImpl(CommonRecordContract.View view) {
        super(view);

        mMediaRecorder = new MediaRecorder();
        mMediaPlayer = new MediaPlayer();
        mRecordTimeArray = new SparseArray<>();
    }

    @Override
    public void getData(String courseId) {
        exeNetworkReq(KJoinMeetingReqId, MeetingAPI.join(courseId).build());
    }

    @Override
    public void startRecord(String filePath, int pos) {
        mPos = pos;
        // 先减去以前录制过的时间 重新设置录制总时间
        if (mTotalTime != 0 && mRecordTimeArray != null) {
            mTotalTime -= mRecordTimeArray.get(pos);
            getView().setTotalRecordTimeTv(Util.getSpecialTimeFormat(mTotalTime, "'", "''"));
        }
        mTime = 0;
        if (mCountDown == null) {
            mCountDown = new CountDown();
            mCountDown.setListener(this);
        }
        File file = new File(filePath);
        mMediaRecorder.reset();
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(OutputFormat.AMR_WB);
        mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_WB);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mCountDown.start(KSixtySecond);
            getView().startRecordState();
        } catch (IOException e) {
            getView().showToast(R.string.record_fail);
        }
    }

    @Override
    public void stopRecord() {
        // 保存录制的时间
        mRecordTimeArray.put(mPos, (int) mTime);
        mTotalTime += mTime;
        getView().setTotalRecordTimeTv(Util.getSpecialTimeFormat(mTotalTime, "'", "''"));
        getView().stopRecordState();
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
        YSLog.d("www", "音频播放地址 = " + filePath);
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
            getView().showToast(R.string.play_fail);
        }
    }

    @Override
    public void stopPlay() {
        if (mMediaPlayer != null) {
            if (mPlayState) {
                mMediaPlayer.stop();
                getView().goneViceLine();
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
            getView().setVoiceLineState(i);
        } else {
            ++mTime;
            getView().setRecordTimeTv(Util.getSpecialTimeFormat(mTime, ":", ""));
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
                JoinMeeting joinMeeting = (JoinMeeting) r.getData();
                List<CourseDetail> courseDetailList = (List<CourseDetail>) joinMeeting.get(TJoinMeeting.course).getList(TCourse.details);
                for (int i = 0; i < courseDetailList.size(); ++i) {
                    CourseDetail courseDetail = courseDetailList.get(i);
                    if (TextUtil.isNotEmpty(courseDetail.getString(TCourseDetail.duration))) {
                        int duration = courseDetail.getInt(TCourseDetail.duration);
                        mTotalTime += duration;
                        mRecordTimeArray.put(i, duration);
                    } else {
                        mRecordTimeArray.put(i, 0);
                    }
                }
                getView().setData(joinMeeting);
                getView().setTotalRecordTimeTv(Util.getSpecialTimeFormat(mTotalTime, "'", "''"));
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }
}

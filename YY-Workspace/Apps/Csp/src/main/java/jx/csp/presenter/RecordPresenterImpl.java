package jx.csp.presenter;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import jx.csp.R;
import jx.csp.contact.RecordContract;
import jx.csp.contact.RecordContract.V;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.util.CacheUtil;
import lib.jx.contract.BasePresenterImpl;
import lib.jx.util.CountDown;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.model.FileSuffix;
import lib.ys.util.FileUtil;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public class RecordPresenterImpl extends BasePresenterImpl<V> implements
        RecordContract.P,
        CountDown.OnCountDownListener {

    private final int KCountDownTime = (int) TimeUnit.MINUTES.toSeconds(10);
    private final int KJoinMeetingReqId = 10;
    private final int KMsgWhatPlayProgress = 1;  // 音频播放进度
    private final int KMsgWhatAmplitude = 2;  // 声音的分贝
    private final int KPlayUIUpdateTime = 500;  // 音频播放UI更新时间
    private final int KAmplitudeUpdateTime = 400;  // 获取分贝的时间间隔

    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;

    private int mTime; // 录制的时间 单位秒
    private CountDown mCountDown;
    private int mPos; // 当前录制的ppt页面下标
    private boolean mRecordState = false; // 录音状态
    private boolean mShowRecordTimeRemind = false; // 是否显示时间不足提醒

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KMsgWhatPlayProgress: {
                    getView().setSeekBarProgress(mMediaPlayer.getCurrentPosition());
                    mHandler.sendEmptyMessageDelayed(KMsgWhatPlayProgress, KPlayUIUpdateTime);
                }
                break;
                case KMsgWhatAmplitude: {
                    getAmplitude();
                }
                break;
            }
        }
    };

    public RecordPresenterImpl(V view) {
        super(view);

        mMediaRecorder = new MediaRecorder();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void getData(String courseId) {
        exeNetworkReq(KJoinMeetingReqId, MeetingAPI.join(courseId).build());
    }

    @Override
    public void startRecord(String filePath, int pos, int alreadyRecordTime) {
        mPos = pos;
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
            mCountDown.start(KCountDownTime - alreadyRecordTime);
            mShowRecordTimeRemind = false;
            getView().setAudioFilePath(filePath);
            getView().startRecordState();
            mRecordState = true;
            getAmplitude();
        } catch (IOException e) {
            YSLog.d(TAG, "record fail msg = " + e.getMessage());
            getView().showToast(R.string.record_fail);
        }
    }

    @Override
    public void stopRecord() {
        mHandler.removeMessages(KMsgWhatAmplitude);
        getView().stopRecordState(mPos, mTime);
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }
        mRecordState = false;
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onlyStopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void jointAudio(String courseId, int pageId, int pos) {
        String folderPath = CacheUtil.getAudioCacheDir() + courseId + File.separator + pageId;
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        // 只拼接amr文件
        ArrayList<File> amrList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(FileSuffix.amr)) {
                amrList.add(files[i]);
            } else {
                FileUtil.delFile(files[i]);
            }
        }
        File[] amrFiles = amrList.toArray(new File[amrList.size()]);
        YSLog.d(TAG, "amrFiles.length(拼接文件的个数) = " + amrFiles.length);
        if (amrFiles.length == 1) {
            YSLog.d(TAG, "只有一段音频不需要拼接");
        } else {
            String fileName = CacheUtil.createAudioFile(courseId, pageId);
            YSLog.d(TAG, "joint file name（拼接后的音频文件名） = " + fileName);

            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                // 按文件的最后编辑时间排序
                File f;
                for (int i = 0; i < amrFiles.length; i++) {
                    for (int j = i + 1; j < amrFiles.length; j++) {
                        if (amrFiles[j].lastModified() < amrFiles[i].lastModified()) {
                            f = amrFiles[i];
                            amrFiles[i] = amrFiles[j];
                            amrFiles[j] = f;
                        }
                    }
                }
                fos = new FileOutputStream(fileName);
                for (int i = 0; i < amrFiles.length; i++) {
                    File file = amrFiles[i];
                    YSLog.d(TAG, "joint files name（正在拼接文件的文件名） = " + i + " = " + file.getName());
                    YSLog.d(TAG, "file last modify time = " + file.lastModified());
                    fis = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int writeLength = 0;
                    // 第一个录音文件的前六位是不需要删除的  之后的文件，去掉前六位
                    if (i == 0) {
                        while ((writeLength = fis.read(bytes)) != -1) {
                            fos.write(bytes, 0, writeLength);
                            fos.flush();
                        }
                    } else {
                        boolean head = true;
                        // 只有文件的头部需要去掉前六位
                        while ((writeLength = fis.read(bytes)) != -1) {
                            if (head) {
                                fos.write(bytes, 9, writeLength - 9);
                                fos.flush();
                                head = false;
                            } else {
                                fos.write(bytes, 0, writeLength);
                                fos.flush();
                            }
                        }
                    }
                }
                // 拼接完后删除原来的文件
                for (int i = 0; i < amrFiles.length; ++i) {
                    FileUtil.delFile(amrFiles[i]);
                }
                // 然后上传音频文件
                getView().uploadJointAudio(fileName, pos);
            } catch (Exception e) {
                e.printStackTrace();
                YSLog.d(TAG, "joint fail msg = " + e.getMessage());
            } finally {
                try {
                    fos.flush();
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
    public void startPlay(String filePath) {
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
            mCountDown.start(KCountDownTime);
            getView().setSeekBarMax(mMediaPlayer.getDuration());
            mHandler.sendEmptyMessageDelayed(KMsgWhatPlayProgress, KPlayUIUpdateTime);
            getView().playState();
            mMediaPlayer.setOnCompletionListener(mp -> {
                mHandler.removeMessages(KMsgWhatPlayProgress);
                getView().setSeekBarProgress(mMediaPlayer.getDuration());
                getView().stopPlayState();
            });
        } catch (IOException e) {
            getView().showToast(R.string.play_fail);
        }
    }

    @Override
    public void mediaPlayProgress(int progress) {
        mMediaPlayer.seekTo(progress);
    }

    @Override
    public void stopPlay() {
        mHandler.removeMessages(KMsgWhatPlayProgress);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();

            }
            mMediaPlayer.reset();
        }
        getView().stopPlayState();
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onCountDown(long remainCount) {
        if (mRecordState) {
            ++mTime;
            getView().setRecordTime(mTime);
            if (remainCount <= TimeUnit.MINUTES.toSeconds(2) && !mShowRecordTimeRemind) {
                getView().setRecordTimeRemind();
                mShowRecordTimeRemind = true;
            }
            if (remainCount == 0) {
                stopRecord();
            }
        }
    }

    @Override
    public void onCountDownErr() {}

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
                getView().setData(joinMeeting);
            } else {
                onNetworkError(id, r.getError());
                getView().onStopRefresh();
                getView().finishRecord();
            }
        }
    }

    private void getAmplitude() {
        if (mMediaRecorder != null && mRecordState) {
            int ratio = mMediaRecorder.getMaxAmplitude();
            int db = 0;// 分贝
            if (ratio > 1) {
                db = (int) (20 * Math.log10(ratio));
            }
            YSLog.d(TAG, "分贝值：" + db);
            int dbLevel;
            if (db >= 90) {
                dbLevel = 6;
            } else if (db >= 80 && db < 90 ) {
                dbLevel = 5;
            } else if (db >= 70 && db < 80) {
                dbLevel = 4;
            } else if (db >= 60 && db < 70) {
                dbLevel = 3;
            } else if (db >= 50 && db < 60) {
                dbLevel = 2;
            } else {
                dbLevel = 1;
            }
            getView().setRecordDbLevel(dbLevel);
            mHandler.sendEmptyMessageDelayed(KMsgWhatAmplitude, KAmplitudeUpdateTime);
        }
    }
}

package jx.csp.contact;

import android.support.annotation.StringRes;

import jx.csp.model.meeting.JoinMeeting;
import lib.yy.contract.IContract;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public interface LiveAudioContract {

    interface V extends IContract.View {

        void setData(JoinMeeting joinMeeting);

        void sendSyncInstruction(int pos);

        /**
         * 直播的时间
         */
        void setLiveTime(String str);

        void startRecordState();

        void stopRecordState();

        /**
         * 直播倒计时
         */
        void setCountDownRemain(boolean show, long l);

        /**
         * 一页录音的时间超过15分钟时，先上传15分钟的音频 加入上传音频列队
         */
        void joinUploadRank(String audioFilePath);

        /**
         * 设置正在录制音频文件的路径
         *
         * @param filePath
         */
        void setAudioFilePath(String filePath);

        void changeRecordIvRes();

        void showToast(@StringRes int id);

        void finishLive();
    }

    interface P extends IContract.Presenter<V> {

        void getData(String courseId);

        void startLive(String courseId, String videoUrl, String imgUrl, int firstClk, int pageNum);

        void changePage(int pos);

        void setWsPos(int pos);

        void startCountDown(long startTime, long stopTime, long serverTime);

        void startLiveRecord(String filePath);

        void stopLiveRecord();

        void uploadVideoPage(String courseId, String courseDetailId);
    }

}

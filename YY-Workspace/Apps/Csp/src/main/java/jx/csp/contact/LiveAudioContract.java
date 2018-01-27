package jx.csp.contact;

import android.support.annotation.StringRes;

import jx.csp.model.meeting.JoinMeeting;
import lib.jx.contract.IContract;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public interface LiveAudioContract {

    interface V extends IContract.View {

        void setData(JoinMeeting joinMeeting);

        void sendSyncInstruction(int pos);

        void setLiveTime();

        void startRecordState();

        void stopRecordState();

        void setRecordTime(int time);

        /**
         * 一页录音的时间超过15分钟时，先上传15分钟的音频 加入上传音频列队
         */
        void joinUploadRank(String audioFilePath, int time);

        /**
         * 设置正在录制音频文件的路径
         *
         * @param filePath
         */
        void setAudioFilePath(String filePath);

        void setLiveStopRemind(int minute);

        void showToast(@StringRes int id);

        void finishLive(boolean toStar);
    }

    interface P extends IContract.Presenter<V> {

        void getData(String courseId);

        void startLive(String courseId, String videoUrl, String imgUrl, int firstClk, int pageNum);

        void setLiveState(boolean state);

        void changePage(int pos);

        void setWsPos(int pos);

        void startLiveRecord(String filePath, boolean changeState);

        void stopLiveRecord(boolean changeState);

        void uploadVideoPage(String courseId, String courseDetailId);

        void startCountDown(int time);

        void stopCountDown();
    }
}

package jx.csp.contact;

import android.support.annotation.StringRes;

import jx.csp.model.meeting.JoinMeeting;
import lib.jx.contract.IContract;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public interface RecordContract {

    interface V extends IContract.View {

        void setData(JoinMeeting joinMeeting);

        void startRecordState();

        void setAudioFilePath(String filePath);

        void setRecordDbLevel(int level);

        void stopRecordState(int pos, int time);

        void canNotContinueRecordState();

        void playState();

        void setSeekBarMax(int max);

        void setSeekBarProgress(int progress);

        void stopPlayState();

        void setRecordTime(int time);

        void setRecordTimeRemind(int minute);

        void uploadJointAudio(String filePath, int pos);

        void showToast(@StringRes int id);

        void finishRecord();
    }

    interface P extends IContract.Presenter<V> {

        void getData(String courseId);

        void startRecord(String filePath, int pos, int alreadyRecordTime);

        void stopRecord();

        void onlyStopRecord();

        void jointAudio(String courseId, int pageId, int pos);

        void startPlay(String filePath);

        void mediaPlayProgress(int progress);

        void pausePlay();

        void continuePlay(int progress);

        void stopPlay();
    }
}

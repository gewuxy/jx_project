package jx.csp.contact;

import android.support.annotation.StringRes;

import jx.csp.model.meeting.Course.PlayType;
import lib.yy.contract.BaseContract;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public interface LiveRecordContract {

    interface View extends BaseContract.BaseView {

        /**
         * 直播的时间
         */
        void setLiveTimeTv(String str);

        void startRecordState();

        void stopRecordState();

        void setOnlineTv(String str);

        /**
         * 直播倒计时
         */
        void setCountDownRemainTv(boolean show, long l);

        /**
         * 一页录音的时间超过15分钟时，先上传15分钟的音频
         */
        void upload(@PlayType int type, String audioFilePath);

        /**
         * 设置正在录制音频文件的路径
         *
         * @param filePath
         */
        void setAudioFilePath(String filePath);

        void changeRecordIvRes();

        void showToast(@StringRes int id);

        void onFinish();
    }

    interface Presenter extends BaseContract.BasePresenter {

        void startCountDown(long startTime, long stopTime);

        void startLiveRecord(String filePath);

        void stopLiveRecord();
    }

}

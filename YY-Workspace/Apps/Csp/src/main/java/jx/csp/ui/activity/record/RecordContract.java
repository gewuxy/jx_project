package jx.csp.ui.activity.record;

import android.support.annotation.StringRes;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public interface RecordContract {

    interface RecordView {

        /**
         * 录制/直播的时间
         */
        void setTotalRecordTimeTv(String str);

        void startRecordState();

        void stopRecordState();

        /**
         * 直播状态/录制时间
         */
        void setRecordTimeTv(String str);

        /**
         * 直播倒计时
         */
        void setTimeRemainTv();

        void showToast(@StringRes int id);

        void setVoiceLineState(int i);

        void goneViceLine();

        void changeRecordIvRes();
    }

    interface RecordPresenter {

        void startCountDown(long startTime, long stopTime);

        void startRecord(String filePath);

        void stopRecord();

        void onDestroy();
    }

}

package jx.csp.ui.activity.record;

import android.support.annotation.StringRes;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public interface LiveRecordContract {

    interface LiveRecordView {

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

        void changeRecordIvRes();

        void showToast(@StringRes int id);

        void onFinish();
    }

    interface LiveRecordPresenter {

        void startCountDown(long startTime, long stopTime);

        void startLiveRecord(String filePath);

        void stopLiveRecord();

        void onDestroy();
    }

}

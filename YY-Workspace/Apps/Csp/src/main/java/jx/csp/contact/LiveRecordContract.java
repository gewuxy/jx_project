package jx.csp.contact;

import android.support.annotation.StringRes;

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

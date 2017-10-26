package jx.csp.ui.activity.record;

import android.support.annotation.StringRes;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public interface CommonRecordContract {

    interface CommonRecordView {

        /**
         * 录制的总时间
         */
        void setTotalRecordTimeTv(String str);

        void startRecordState();

        void stopRecordState();

        /**
         * 录制时间
         */
        void setRecordTimeTv(String str);

        void showToast(@StringRes int id);

        void setVoiceLineState(int i);

        void goneViceLine();
    }

    interface CommonRecordPresenter {

        void setBeforeRecordTime(int t);

        void startRecord(String filePath);

        void stopRecord();

        void onDestroy();
    }

}

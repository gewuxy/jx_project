package jx.csp.contact;

import android.support.annotation.StringRes;

import lib.yy.contract.IContract;

/**
 * @author CaiXiang
 * @since 2017/10/9
 */

public interface CommonRecordContract {

    interface View extends IContract.View {

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

    interface Presenter extends IContract.Presenter<View> {

        void setBeforeRecordTime(int t);

        void startRecord(String filePath);

        void stopRecord();
    }

}

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

    interface P extends IContract.Presenter<V> {

        void getData(String courseId);

        void startRecord(String filePath, int pos);

        void stopRecord();
    }

}

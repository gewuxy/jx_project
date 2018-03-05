package jx.doctor.ui.activity.meeting.play.contract;

import android.content.Context;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */

public interface RebContact {

    interface View extends BasePptContract.View {
        void playProgress(String time, int progress);

        void setTime(String time);
    }

    interface Presenter extends BasePptContract.Presenter<View> {

        void toOverview(Context context, String title, int code);

        void changeTime(int progress);
    }

}

package jx.doctor.ui.activity.meeting.play.contract;

import android.content.Context;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */

public interface RebContact {

    interface View extends BasePptContract.View {

    }

    interface Presenter extends BasePptContract.Presenter<View> {

        void start(int index);

        void stop();

        void toOverview(Context context, String title, int code);
    }

}

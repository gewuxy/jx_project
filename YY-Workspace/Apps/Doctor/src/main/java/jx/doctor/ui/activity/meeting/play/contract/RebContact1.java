package jx.doctor.ui.activity.meeting.play.contract;

import android.content.Context;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */

public interface RebContact1 {

    interface View extends BasePptContract1.View {

    }

    interface Presenter extends BasePptContract1.Presenter<View> {

        void toOverview(Context context, String title, int code);
    }

}

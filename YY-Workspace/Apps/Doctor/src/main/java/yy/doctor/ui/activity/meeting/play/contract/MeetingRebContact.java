package yy.doctor.ui.activity.meeting.play.contract;

import lib.yy.contract.IContract;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */

public interface MeetingRebContact {

    interface View extends BasePptContract.View {

    }

    interface Presenter extends BasePptContract.Presenter<View> {

        void start(int index);

        void stop();
    }

}

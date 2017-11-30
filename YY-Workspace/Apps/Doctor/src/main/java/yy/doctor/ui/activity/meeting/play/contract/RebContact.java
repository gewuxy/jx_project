package yy.doctor.ui.activity.meeting.play.contract;

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
    }

}

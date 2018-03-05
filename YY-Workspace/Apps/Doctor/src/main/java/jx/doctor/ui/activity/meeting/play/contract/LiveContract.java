package jx.doctor.ui.activity.meeting.play.contract;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */
public interface LiveContract {

    interface View extends PptLiveContract.View {

        void startPull();

        void nextItem();
    }

    interface Presenter extends BasePlayContract.Presenter<View> {

    }
}

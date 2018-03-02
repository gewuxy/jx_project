package jx.doctor.ui.activity.meeting.play.contract;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */
public interface LiveContract1 {

    interface View extends PptLiveContract1.View {

        void startPull();

        void nextItem();
    }

    interface Presenter extends BasePlayContract1.Presenter<View> {

    }
}

package jx.doctor.ui.activity.meeting.play.contract;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */
public interface BasePptContract {

    interface View extends BasePlayContract.View {

    }

    interface Presenter<V extends View> extends BasePlayContract.Presenter<V> {

    }
}

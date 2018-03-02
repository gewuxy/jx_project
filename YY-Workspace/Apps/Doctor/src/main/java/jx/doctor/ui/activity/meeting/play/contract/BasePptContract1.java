package jx.doctor.ui.activity.meeting.play.contract;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */
public interface BasePptContract1 {

    interface View extends BasePlayContract1.View {

    }

    interface Presenter<V extends View> extends BasePlayContract1.Presenter<V> {

    }
}

package jx.doctor.ui.activity.meeting.play.contract;

import jx.doctor.model.meet.ppt.Course;

/**
 * @auther : GuoXuan
 * @since : 2017/10/30
 */

public interface PptLiveContract {

    interface View extends BasePptContract.View {

        void addCourse(Course course);

        void refresh(Course course);
    }

    interface Presenter extends BasePptContract.Presenter<View> {

    }

}

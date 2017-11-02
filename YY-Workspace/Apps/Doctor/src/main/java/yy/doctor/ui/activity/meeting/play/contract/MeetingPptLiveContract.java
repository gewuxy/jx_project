package yy.doctor.ui.activity.meeting.play.contract;

import yy.doctor.model.meet.ppt.Course;

/**
 * @auther : GuoXuan
 * @since : 2017/10/30
 */

public interface MeetingPptLiveContract {

    interface View extends MeetingPptContract.View {

        void addCourse(Course course);

    }

    interface Presenter extends MeetingPptContract.Presenter {

    }

}

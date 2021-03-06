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

        /**
         * 更新在线人数
         */
        void setTextOnline(int onlineNum);
    }

    interface Presenter extends BasePptContract.Presenter<View> {

    }

}

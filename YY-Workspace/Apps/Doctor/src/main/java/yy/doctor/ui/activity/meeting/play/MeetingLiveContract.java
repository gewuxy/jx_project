package yy.doctor.ui.activity.meeting.play;

import yy.doctor.model.meet.ppt.PPT;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */
public class MeetingLiveContract {

    interface View extends BaseView {
        void initView(PPT ppt);

        void addCourse(int position);
    }

    interface Presenter {
        /**
         * 获取网络数据
         */
        void getDataFromNet(String meetId, String moduleId);
    }
}

package yy.doctor.ui.activity.meeting.play.contract;

import lib.ys.ui.interfaces.opt.ICommonOpt;
import lib.yy.contract.BaseContract;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.PPT;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */
public interface MeetingLiveContract {

    interface View extends BaseContract.BaseView,ICommonOpt {

        void initView(PPT ppt);

        void addCourse(Course course);
    }

    interface Presenter extends BaseContract.BasePresenter{
        /**
         * 获取网络数据
         */
        void getDataFromNet(String meetId, String moduleId);
    }
}

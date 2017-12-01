package jx.doctor.ui.activity.meeting.play.contract;

import lib.ys.ui.interfaces.opt.ICommonOpt;
import lib.jx.contract.IContract;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.ui.frag.meeting.PPTRebFrag;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */
public interface LiveContract {

    interface View extends BasePlayContract.View, ICommonOpt {

        void initView(PPT ppt);

        void addCourse(Course course);

        void refresh(Course course);

        void finishCount();

        PPTRebFrag getPptFrag();

        void setTextOnline(int onlineNum);

        void nextItem();
    }

    interface Presenter extends IContract.Presenter<View> {
        /**
         * 获取网络数据
         */
        void getDataFromNet(String meetId, String moduleId);

        void starCount();

        void mediaStop();
    }
}

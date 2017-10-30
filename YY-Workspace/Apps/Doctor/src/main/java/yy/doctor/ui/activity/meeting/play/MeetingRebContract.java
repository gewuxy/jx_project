package yy.doctor.ui.activity.meeting.play;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import lib.ys.ui.interfaces.opt.ICommonOpt;
import lib.yy.contract.BaseContract;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.PPT;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */
public interface MeetingRebContract {

    interface View extends BaseContract.BaseView,ICommonOpt {
        /**
         * 初始化竖屏
         */
        void portraitInit(PPT ppt, List<Course> courses);

        /**
         * 播放状态
         */
        void onPlayState(boolean state);

        /**
         * 播放视频投射的控件
         */
        PLVideoTextureView getTextureView();

        /**
         * 刷新某一条的数据
         */
        void invalidate(int position);

        /**
         * 下一页
         */
        void setNextItem();

        /**
         * 计时结束
         */
        void finishCount();
    }

    interface Presenter extends BaseContract.BasePresenter {

        /**
         * 播放
         */
        void playMedia(int position);

        /**
         * 暂停
         */
        void stopMedia();

        /**
         * 状态转换
         */
        void toggle();

        /**
         * 开始计时
         */
        void starCount();
    }
}

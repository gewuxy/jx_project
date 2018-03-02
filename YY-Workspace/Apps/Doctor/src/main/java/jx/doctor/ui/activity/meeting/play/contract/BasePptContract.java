package jx.doctor.ui.activity.meeting.play.contract;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.PPT;
import lib.jx.contract.IContract;
import lib.ys.ui.interfaces.opt.ICommonOpt;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */
public interface BasePptContract {

    interface View extends BasePlayContract.View, ICommonOpt {
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
         * 下一页
         */
        void setNextItem();

        /**
         * 计时结束(横屏)
         */
        void finishCount();
    }

    interface Presenter<V extends View> extends IContract.Presenter<V> {

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
        void toggle(int index);

        /**
         * 开始计时
         */
        void starCount();

        /**
         * 停止计时
         */
        void stopCount();

        /**
         * 图片3秒下一页
         */
        void imgNext();
    }
}

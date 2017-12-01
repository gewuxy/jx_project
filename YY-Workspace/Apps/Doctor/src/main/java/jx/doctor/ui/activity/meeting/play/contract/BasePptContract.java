package jx.doctor.ui.activity.meeting.play.contract;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import lib.ys.ui.interfaces.opt.ICommonOpt;
import lib.jx.contract.IContract;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.PPT;

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
         * 刷新某一条的数据
         */
        void invalidate(int position);

        /**
         * 下一页
         */
        void setNextItem();

        /**
         * 计时结束(横屏)
         */
        void finishCount();

        /**
         * 更新在线人数
         */
        void setTextOnline(int onlineNum);
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
         * 图片3秒下一页
         */
        void imgNext();
    }
}

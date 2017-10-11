package yy.doctor.ui.activity.meeting.play;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.Map;

import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.Submit;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */
public interface MeetingREPContract {

    interface View extends BaseView {
        /**
         * 初始化竖屏
         */
        void portraitInit(PPT ppt);

        /**
         * 初始化横屏
         */
        void landscapeInit(PPT ppt);

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

    interface Presenter {
        /**
         * 获取网络数据
         */
        void getDataFromNet(String meetId, String moduleId);

        /**
         * 横屏(获取数据)
         */
        void landscapeScreen();

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

        void onDestroy();
    }
}

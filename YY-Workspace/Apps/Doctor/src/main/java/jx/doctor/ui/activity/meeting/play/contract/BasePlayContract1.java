package jx.doctor.ui.activity.meeting.play.contract;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import jx.doctor.model.meet.ppt.PPT;
import lib.jx.contract.IContract;

/**
 * @auther : GuoXuan
 * @since : 2017/11/27
 */

public interface BasePlayContract1 {

    interface View extends IContract.View {

        /**
         * 网络请求成功
         */
        void onNetworkSuccess(PPT ppt);

        /**
         * 竖屏
         */
        void portrait();

        /**
         * 横屏
         */
        void landscape();

        /**
         * 横屏拦截(操作)
         */
        void landscapeIntercept();

        /**
         * 默认显示结束
         */
        void countFinish();

        /**
         * 播放状态
         */
        void onPlayState(boolean state);

        /**
         * 播放视频投射的控件
         */
        PLVideoTextureView getTextureView();

        /**
         * 下一页(自动)
         */
        void setNextItem();
    }

    interface Presenter<V extends BasePlayContract1.View> extends IContract.Presenter<V> {

        void setData(String meetId, String moduleId);

        /**
         * 发起网络请求
         */
        void getDataFromNet();

        /**
         * 开始计时
         */
        void startCount();

        /**
         * 停止计时
         */
        void stopCount();

        /**
         * 点击控制按钮
         */
        void toggle(int index);

        /**
         * 播放
         */
        void playMedia(int position);

        /**
         * 暂停
         */
        void stopMedia();

        /**
         * 图片3秒下一页
         */
        void imgNext();

    }

}

package jx.doctor.ui.activity.meeting.play.contract;

import android.support.annotation.DrawableRes;

import lib.yy.contract.IContract;

/**
 * @auther : GuoXuan
 * @since : 2017/11/27
 */

public interface BasePlayContract {

    interface View extends IContract.View {

        /**
         * 上一页
         */
        void toLeft();

        /**
         * 下一页
         */
        void toRight();

        /**
         * 竖屏
         */
        void portrait();

        /**
         * 横屏
         */
        void landscape();

        /**
         * 点击控制按钮
         */
        void toggle();

        /**
         * 控制按钮的选择器
         */
        @DrawableRes
        int getControlResId();

        /**
         * 横屏时操作
         */
        void showLandscapeView();

        /**
         * 横屏右上角按钮
         */
        boolean getNavBarLandscape();
    }

}
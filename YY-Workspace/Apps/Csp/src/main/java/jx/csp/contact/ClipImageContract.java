package jx.csp.contact;

import android.graphics.Bitmap;

import lib.ys.ui.other.NavBar;
import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public interface ClipImageContract {
    interface V extends IContract.View {

        /**
         * 头部的设置
         *
         * @param bar
         */
        void setNavBar(NavBar bar);

        void setNavBarTextColor();

        /**
         * 请求成功的处理
         */
        void setSuccessProcessed();
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 上传头像
         *
         * @param bitmap
         */
        void upLoadAvatar(Bitmap bitmap);
    }
}

package jx.csp.contact;

import android.graphics.Bitmap;

import jx.csp.presenter.PresenterEx;
import jx.csp.ui.ViewEx;
import lib.ys.ui.other.NavBar;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public interface ClipImageContract {
    interface V extends ViewEx{

        /**
         * 头部的设置
         * @param bar
         */
        void setNavBar(NavBar bar);

        void setNavBarTextColor();

        /**
         * 请求成功的处理
         */
        void setSuccessProcessed();
    }

    interface P extends PresenterEx{

        void getupLoadAvatar(Bitmap bitmap);
    }
}

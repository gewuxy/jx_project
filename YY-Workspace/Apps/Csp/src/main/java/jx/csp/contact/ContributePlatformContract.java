package jx.csp.contact;

import java.util.ArrayList;

import jx.csp.model.contribute.Platform;
import jx.csp.presenter.PresenterEx;
import jx.csp.ui.ViewEx;
import lib.ys.ui.other.NavBar;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface ContributePlatformContract {
    interface V extends ViewEx{

        /**
         * 点击图标, 提示文字
         * @param bar
         */
        void showDialog(NavBar bar);

        /**
         * 获取单位号条目
         * @param position
         * @param isSelected
         */
        void getAcceptIdItem(int position, boolean isSelected);

        /**
         * 改变按钮状态
         */
        void changeButtonStatus();

        void stopRefreshItem();
    }

    interface P extends PresenterEx {

        /**
         * 投稿的服务器请求
         */
        void clickContributeReq(ArrayList<Platform> platformArrayList, Platform platform);

    }
}

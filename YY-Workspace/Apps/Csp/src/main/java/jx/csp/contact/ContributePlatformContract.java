package jx.csp.contact;

import java.util.ArrayList;

import jx.csp.model.contribute.Platform;
import lib.ys.ui.other.NavBar;
import lib.yy.contract.BaseContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface ContributePlatformContract {

    interface V extends BaseContract.BaseView{

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

    interface P extends BaseContract.BasePresenter {

        /**
         * 投稿的服务器请求
         */
        void clickContributeReq(ArrayList<Platform> platformArrayList, Platform platform);

    }
}

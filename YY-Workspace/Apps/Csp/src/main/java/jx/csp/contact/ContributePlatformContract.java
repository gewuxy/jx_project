package jx.csp.contact;

import java.util.ArrayList;

import jx.csp.model.Platform;
import lib.ys.ui.other.NavBar;
import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface ContributePlatformContract {

    interface V extends IContract.View {

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

        void onFinish();
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 单位号投稿
         *
         * @param platformArrayList
         * @param platform
         */
        void clickContributeNetworkReq(ArrayList<Platform> platformArrayList, Platform platform);

    }
}

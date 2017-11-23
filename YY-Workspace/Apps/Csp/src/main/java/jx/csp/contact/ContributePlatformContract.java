package jx.csp.contact;

import jx.csp.model.Platform;
import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface ContributePlatformContract {

    interface V extends IContract.View {

        /**
         * 改变按钮状态
         */
        void changeButtonStatus(boolean enabled);

        void onFinish();

    }

    interface P extends IContract.Presenter<V> {

        /**
         * 设置单位号条目
         *
         * @param position
         * @param isSelected
         */
        void addItem(Platform position, boolean isSelected);

        /**
         * 单位号投稿
         *
         * @param courseId
         */
        void clickContribute(String courseId);

    }
}

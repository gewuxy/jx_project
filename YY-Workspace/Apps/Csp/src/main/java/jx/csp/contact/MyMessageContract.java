package jx.csp.contact;

import jx.csp.model.Profile.TProfile;
import lib.jx.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public interface MyMessageContract {
    interface V extends IContract.View {

        /**
         * 设置数据
         *
         * @param text
         */
        void setData(String text);

        /**
         * 获取输入框数据
         */
        void getText();

    }

    interface P extends IContract.Presenter<V> {

        /**
         * 保存个人信息
         *
         * @param id
         * @param text
         */
        void savePersonMessage(int id, String text);

        /**
         * 保存数据
         */
        void saveLocal(TProfile profile);
    }
}

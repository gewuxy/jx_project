package jx.csp.contact;

import lib.jx.contract.IContract.Presenter;
import lib.jx.contract.IContract.View;

/**
 * @auther HuoXuYu
 * @since 2018/1/27
 */

public interface WatchPwdContract {
    interface V extends View {

        /**
         *  设置有密码的界面
         */
        void setExistingPwd();

        /**
         *  设置无密码的界面
         */
        void setUnPwd();

        /**
         *  设置密码
         * @param password
         */
        void setPwdText(String password);
    }

    interface P extends Presenter<V> {

        /**
         *  获取密码
         * @param id
         * @param courseId
         * @param password
         */
        void getPassword(String id, String courseId, String password);

        /**
         *  随机密码
         * @param length
         */
        String getRandomPwd(int length);

    }


}

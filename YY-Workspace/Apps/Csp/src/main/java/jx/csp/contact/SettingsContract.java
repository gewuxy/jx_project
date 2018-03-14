package jx.csp.contact;

import android.content.Context;

import lib.jx.contract.IContract;

/**
 * @auther HuoXuYu
 * @since 2017/10/26
 */

public interface SettingsContract {

    interface V extends IContract.View {

        /**
         * 退出登录Dialog
         */
        void logoutDialog();

        void closePage();
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 修改密码
         */
        void changePassWord();

        /**
         * 启动登出服务
         */
        void startLogoutService(Context context);

        /**
         * 退出登录
         */
        void logout(Context context);
    }
}

package jx.csp.contact;

import android.content.Context;

import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface SettingsContract {

    interface V extends IContract.View {

        void refreshItem(int related);

        void closePage();
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 修改密码
         */
        void changePassWord();

        /**
         * 清除缓存
         */
        void clearCache(Context context, int related, String resId, String cancel, String... folderPath);

        /**
         * 获取路径下的文件大小
         *
         * @param path
         */
        String getFolderSize(String... path);

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

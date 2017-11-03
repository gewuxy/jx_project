package jx.csp.contact;

import android.content.Context;
import android.support.annotation.StringRes;

import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface SettingsContract {

    interface V extends IContract.View {

        /**
         * 修改密码
         */
        void changePassWord();

        /**
         * 清除缓存
         */
        void clearCache(int related, @StringRes int resId, String... folderPath);

        /**
         * 退出登录
         */
        void logout();
    }

    interface P extends IContract.Presenter<V> {

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
    }
}

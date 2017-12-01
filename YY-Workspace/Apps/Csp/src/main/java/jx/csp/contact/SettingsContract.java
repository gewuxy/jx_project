package jx.csp.contact;

import android.content.Context;

import jx.csp.ui.activity.me.SettingsActivity.RelatedId;
import lib.jx.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface SettingsContract {

    interface V extends IContract.View {

        /**
         * 清除缓存Dialog
         */
        void clearCacheDialog(@RelatedId int id, int resId, String... folderPath);

        void refreshItem(@RelatedId int id);

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
         * 清除缓存
         */
        void clearCache(@RelatedId int id, String... folderPath);

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

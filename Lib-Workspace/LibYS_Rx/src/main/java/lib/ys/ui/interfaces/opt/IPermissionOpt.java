package lib.ys.ui.interfaces.opt;

import android.support.annotation.NonNull;

import lib.ys.util.permission.Permission;

/**
 * @auther yuansui
 * @since 2017/5/8
 */

public interface IPermissionOpt {
    /**
     * 检查权限
     *
     * @param ps
     * @param code
     * @return
     */
    boolean checkPermission(int code, @Permission String... ps);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}

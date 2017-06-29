package lib.ys.ui.interfaces.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import lib.ys.ui.interfaces.opt.IPermissionOpt;
import lib.ys.util.permission.CheckTask;
import lib.ys.util.permission.OnPermissionListener;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionChecker;

/**
 * @auther yuansui
 * @since 2017/5/8
 */

public class PermissionOpt implements IPermissionOpt {

    private Context mContext;
    private OnPermissionListener mLsn;

    public PermissionOpt(@NonNull Context context, OnPermissionListener l) {
        mContext = context;
        mLsn = l;
    }

    @Override
    public boolean checkPermission(int code, @Permission String... ps) {
        if (PermissionChecker.allow(mContext, ps)) {
            return true;
        }

        CheckTask task = CheckTask.newBuilder()
                .permissions(ps)
                .code(code)
                .listener(mLsn)
                .build();

        return PermissionChecker.inst().check(task);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int code = 0;
        if (requestCode < 65536) {
            // 表示为activity自身发出的请求
            code = requestCode;
        } else {
            // 表示为fragment发出的请求
            code = requestCode & 0xffff;
        }
        PermissionChecker.inst().onRequestPermissionsResult(mLsn, code, permissions, grantResults);
    }
}

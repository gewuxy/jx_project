package lib.ys.util.permission;

import java8.lang.FunctionalInterface;

@FunctionalInterface
public interface OnPermissionListener {
    void onPermissionResult(int code, @PermissionResult int result);
}
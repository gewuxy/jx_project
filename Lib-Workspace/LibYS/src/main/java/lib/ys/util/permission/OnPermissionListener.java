package lib.ys.util.permission;

@FunctionalInterface
public interface OnPermissionListener {
    void onPermissionResult(int code, @PermissionResult int result);
}
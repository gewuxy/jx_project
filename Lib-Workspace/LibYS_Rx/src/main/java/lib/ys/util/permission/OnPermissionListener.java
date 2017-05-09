package lib.ys.util.permission;

public interface OnPermissionListener {
    void onPermissionResult(int code, @PermissionResult int result);
}
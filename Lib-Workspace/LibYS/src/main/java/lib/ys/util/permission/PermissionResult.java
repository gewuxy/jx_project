package lib.ys.util.permission;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        PermissionResult.granted,
        PermissionResult.denied,
        PermissionResult.never_ask,
})
@Retention(RetentionPolicy.SOURCE)
public @interface PermissionResult {
    /**
     * 允许
     */
    int granted = 0;
    /**
     * 拒绝
     */
    int denied = 1;
    /**
     * 拒绝后不再提示, 需要权限解释
     */
    int never_ask = 2;
}
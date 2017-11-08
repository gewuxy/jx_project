package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        AppType.inland,
        AppType.overseas
})
@Retention(RetentionPolicy.SOURCE)
public @interface AppType {
    int inland = 0; // 国内
    int overseas = 1; // 海外
}
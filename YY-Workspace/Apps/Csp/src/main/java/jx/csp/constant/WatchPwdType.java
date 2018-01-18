package jx.csp.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther HuoXuYu
 * @since 2018/1/18
 */
@StringDef({
        WatchPwdType.setPwd,
        WatchPwdType.delete
})
@Retention(RetentionPolicy.SOURCE)
public @interface WatchPwdType {
    String setPwd = "1";
    String delete = "2";
}

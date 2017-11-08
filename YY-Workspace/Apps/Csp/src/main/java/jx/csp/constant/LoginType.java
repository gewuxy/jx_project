package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 登录的type
 */
@IntDef({
        LoginType.wechat,
        LoginType.sina,
        LoginType.facebook,
        LoginType.twitter,
        LoginType.yaya,
        LoginType.phone,
        LoginType.email,
})
@Retention(RetentionPolicy.SOURCE)
public @interface LoginType {
    int wechat = 1;
    int sina = 2;
    int facebook = 3;
    int twitter = 4;
    int yaya = 5;
    int phone = 6;
    int email = 7;
}
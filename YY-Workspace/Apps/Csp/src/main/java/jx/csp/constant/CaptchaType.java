package jx.csp.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 获取验证码，登录的type= 0 ，个人中心的type = 1，为string类型
 */
@StringDef({
        CaptchaType.fetch,
        CaptchaType.re_fetch,
})
@Retention(RetentionPolicy.SOURCE)
public @interface CaptchaType {
    String fetch = "0";
    String re_fetch = "1";
}
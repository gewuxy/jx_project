package jx.csp;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public interface Constants extends BaseConstants {

    String KAppId = "wx83d3ea20a714b660";

    @StringDef({
            WXType.login,
            WXType.bind,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface WXType {
        String login = "login";
        String bind = "bind";
    }

    //获取验证码，登录的type= 0 ，个人中心的type = 1，为string类型
    @StringDef({
            CaptchaType.fetch,
            CaptchaType.re_fetch,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface CaptchaType {
        String fetch = "0";
        String re_fetch = "1";
    }

    @IntDef({
            RecordType.live,
            RecordType.common
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface RecordType {
        int live = 1;  // 直播
        int common = 2;  // 普通录制
    }

    //登录的type
    @IntDef({
            LoginType.wechat_login,
            LoginType.weibo_login,
            LoginType.facebook_login,
            LoginType.twitter_login,
            LoginType.yaya_login,
            LoginType.phone_login,
            LoginType.email_login,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface LoginType {
        int wechat_login = 1;
        int weibo_login = 2;
        int facebook_login = 3;
        int twitter_login = 4;
        int yaya_login = 5;
        int phone_login = 6;
        int email_login = 7;
    }
}

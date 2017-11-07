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

    //登录的type
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
    @interface LoginType {
        int wechat = 1;
        int sina = 2;
        int facebook = 3;
        int twitter = 4;
        int yaya = 5;
        int phone = 6;
        int email = 7;
    }

    interface PageConstants {
        int KPage = 1;  // 起始页页数
        int KPageSize = 20;  //分页加载时 每页加载的条目数
    }
}

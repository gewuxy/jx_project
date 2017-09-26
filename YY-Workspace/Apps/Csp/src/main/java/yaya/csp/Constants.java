package yaya.csp;

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

    @IntDef({
            CaptchaType.fetch,
            CaptchaType.re_fetch,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface CaptchaType {
        int fetch = 0;
        int re_fetch = 1;
    }
}

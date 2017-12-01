package jx.doctor;

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
            MeetStateText.under_way,
            MeetStateText.not_started,
            MeetStateText.retrospect,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface MeetStateText {
        String under_way = "进行中";
        String not_started = "未开始";
        String retrospect = "精彩回顾";
    }

    interface PageConstants {
        int KPage = 1;  // 起始页页数
        int KPageSize = 20;  //分页加载时 每页加载的条目数
    }

    @IntDef({
            DateUnit.hour,
            DateUnit.minute,
            DateUnit.second,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface DateUnit {
        int hour = 0;
        int minute = 1;
        int second = 2;
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

    @StringDef({
            WXType.login,
            WXType.bind,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface WXType {
        String login = "login";
        String bind = "bind";
    }
}

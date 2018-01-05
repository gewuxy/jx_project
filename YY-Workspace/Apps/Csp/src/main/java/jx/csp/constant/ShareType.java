package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */
@IntDef({
        ShareType.wechat,
        ShareType.wechat_friend,
        ShareType.qq,
        ShareType.linkedin,
        ShareType.sina,
        ShareType.facebook,
        ShareType.twitter,
        ShareType.whatsapp,
        ShareType.line,
        ShareType.sms,
        ShareType.dingding,
        ShareType.contribute,
})
@Retention(RetentionPolicy.SOURCE)
public @interface ShareType {
    int wechat = 0;
    int wechat_friend = 1;
    int qq = 2;
    int linkedin = 3;
    int sina = 4;
    int facebook = 5;
    int twitter = 6;
    int whatsapp = 7;
    int line = 8;
    int sms = 9;
    int dingding = 10;
    int contribute = 11;
}
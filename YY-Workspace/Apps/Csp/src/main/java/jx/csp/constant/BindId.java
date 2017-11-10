package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 第三方绑定的type id
 */
@IntDef({
        BindId.wechat,
        BindId.sina,
        BindId.facebook,
        BindId.twitter,
        BindId.yaya,
        BindId.phone,
        BindId.email,
})
@Retention(RetentionPolicy.SOURCE)
public @interface BindId {
    int wechat = 1;
    int sina = 2;
    int facebook = 3;
    int twitter = 4;
    int yaya = 5;
    int phone = 6;
    int email = 7;
}
package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther HuoXuYu
 * @since 2017/9/21
 */
@IntDef({
        FormType.text,
        FormType.text_intent,
        FormType.text_intent_bind,
        FormType.text_clear_cache,

        FormType.divider,
        FormType.divider_margin,
        FormType.divider_large,

        FormType.et,
        FormType.et_intent,
        FormType.et_pwd,
        FormType.et_phone_number,
        FormType.et_captcha,
})
@Retention(RetentionPolicy.SOURCE)
public @interface FormType {
    int text = 0;
    int text_intent = 1;
    int text_intent_bind = 2;
    int text_clear_cache = 3;

    int divider_margin = 10;
    int divider_large = 11;
    int divider = 12;

    int et = 20;
    int et_intent = 21;
    int et_pwd = 22;
    int et_phone_number = 23;
    int et_captcha = 24;
}

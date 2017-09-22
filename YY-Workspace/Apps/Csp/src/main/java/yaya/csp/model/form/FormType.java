package yaya.csp.model.form;

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

        FormType.divider,
        FormType.divider_large,
        FormType.divider_margin,

        FormType.et,
        FormType.et_intent,
})
@Retention(RetentionPolicy.SOURCE)
public @interface FormType {
    int text = 0;
    int text_intent = 1;
    int text_intent_me = 2;

    int divider = 10;
    int divider_large = 11;
    int divider_margin = 12;

    int et = 20;
    int et_intent = 21;
}

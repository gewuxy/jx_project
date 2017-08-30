package yy.doctor.model.form;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
@IntDef({
        FormType.text,
        FormType.text_intent,
        FormType.text_dialog,
        FormType.text_intent_no_name,
        FormType.text_intent_me,

        FormType.divider,
        FormType.divider_large,
        FormType.divider_margin,

        FormType.et,
        FormType.et_intent,
        FormType.et_pwd,
        FormType.et_phone_number,
        FormType.et_captcha,

        FormType.profile_checkbox,

        FormType.toggle_button,
})
@Retention(RetentionPolicy.SOURCE)
public @interface FormType {
    int text = 0;
    int text_intent = 1;
    int text_dialog = 2;
    int text_intent_no_name = 3;
    int text_intent_me = 5;

    int divider = 10;
    int divider_large = 11;
    int divider_margin = 12;

    int et = 20;
    int et_intent = 21;
    int et_pwd = 22;
    int et_phone_number = 23;
    int et_captcha = 24;

    int profile_checkbox = 30;

    int toggle_button = 40;
}

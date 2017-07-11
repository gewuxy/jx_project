package yy.doctor.model.form;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
@IntDef({
        FormType.content,
        FormType.content_text,
        FormType.text,
        FormType.text_intent,

        FormType.divider,
        FormType.divider_large,

        FormType.et,
        FormType.et_intent,
        FormType.et_register,
        FormType.et_number,
        FormType.et_phone_number,
        FormType.et_email,

        FormType.profile_checkbox,

})
@Retention(RetentionPolicy.SOURCE)
public @interface FormType {
    int content = 0;
    int content_text = 1;
    int text = 2;
    int text_intent = 3;
    int text_dialog = 4;
    int text_register_intent = 5;

    int divider = 10;
    int divider_large = 11;

    int et = 20;
    int et_intent = 21;
    int et_register = 22;
    int et_register_pwd = 23;
    int et_number = 24;
    int et_phone_number = 25;
    int et_email = 26;

    int profile_checkbox = 30;

}

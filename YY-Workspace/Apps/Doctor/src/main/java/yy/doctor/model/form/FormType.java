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

        FormType.profile_checkbox,

})
@Retention(RetentionPolicy.SOURCE)
public @interface FormType {
    int content = 0;
    int content_text = 1;
    int text = 2;
    int text_intent = 3;

    int divider = 10;
    int divider_large = 11;

    int et = 20;
    int et_intent = 21;
    int et_register = 22;

    int profile_checkbox = 30;

}

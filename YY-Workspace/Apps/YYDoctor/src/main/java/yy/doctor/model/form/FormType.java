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
        FormType.divider,
        FormType.divider_large,
})
@Retention(RetentionPolicy.SOURCE)
public @interface FormType {
    int content = 0;
    int divider = 1;
    int divider_large = 2;
}

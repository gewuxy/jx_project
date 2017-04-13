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
        FormType.content_no_img_tx,
        FormType.content_no_img,
})
@Retention(RetentionPolicy.SOURCE)
public @interface FormType {
    int content = 0;
    int divider = 1;
    int divider_large = 2;
    int content_no_img_tx = 3;
    int content_no_img = 4;
}

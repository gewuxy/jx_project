package yy.doctor.model.form.menu;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
@IntDef({
        MenuType.child,
        MenuType.group,
        MenuType.divider,
})
@Retention(RetentionPolicy.SOURCE)
public @interface MenuType {
    int child = 0;
    int group = 1;
    int divider = 2;
}

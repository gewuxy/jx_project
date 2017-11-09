package jx.csp.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther yuansui
 * @since 2017/11/8
 */
@StringDef({
        MetaValue.app_type,
})
@Retention(RetentionPolicy.SOURCE)
public @interface MetaValue {
    String app_type = "APP_TYPE";
    String app_name = "APP_NAME";
}

package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther Huoxuyu
 * @since 2017/12/14
 */

@IntDef({
        VipType.norm,
        VipType.advanced,
        VipType.profession
})
@Retention(RetentionPolicy.SOURCE)
public @interface VipType {
    int norm = 1;
    int advanced =2;
    int profession = 3;
}

package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther : GuoXuan
 * @since : 2018/2/1
 */
@IntDef({
        FiltrateType.all,
        FiltrateType.ppt,
        FiltrateType.photo,
})
@Retention(RetentionPolicy.SOURCE)
public @interface FiltrateType {
    int all = 0; // 全部
    int ppt = 1; // PPT
    int photo = 2; // 照片
}

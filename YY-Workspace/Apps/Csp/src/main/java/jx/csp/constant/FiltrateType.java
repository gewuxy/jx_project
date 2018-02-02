package jx.csp.constant;

import android.support.annotation.IntDef;

/**
 * @auther : GuoXuan
 * @since : 2018/2/1
 */
@IntDef({
        FiltrateType.all,
        FiltrateType.ppt,
        FiltrateType.photo,
})
public @interface FiltrateType {
    int all = 0; // 全部
    int ppt = 1; // PPT
    int photo = 2; // 照片
}

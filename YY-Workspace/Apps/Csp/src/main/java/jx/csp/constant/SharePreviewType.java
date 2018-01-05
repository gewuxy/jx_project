package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther ${HuoXuYu}
 * @since 2018/1/3
 */
@IntDef({
        SharePreviewType.preview,
        SharePreviewType.pwd,
        SharePreviewType.copy,
        SharePreviewType.delete,
})
@Retention(RetentionPolicy.SOURCE)
public @interface SharePreviewType {
    int preview = 0;
    int pwd = 1;
    int copy = 2;
    int delete = 3;
}

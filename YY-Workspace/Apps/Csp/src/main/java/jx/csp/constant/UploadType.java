package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 上传照片选择的类型
 *
 * @auther : GuoXuan
 * @since : 2018/2/5
 */
@IntDef({
        UploadType.camera,
        UploadType.photo,
})
@Retention(RetentionPolicy.SOURCE)
public @interface UploadType {
    int camera = 0; // 照相机
    int photo = 1; // 相册
}

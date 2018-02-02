package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        SourceType.yaya,
        SourceType.ppt,
        SourceType.card,
        SourceType.red_packet,
        SourceType.photo,
})
@Retention(RetentionPolicy.SOURCE)
public @interface SourceType {
    int yaya = 0; // YaYa
    int ppt = 1; // 网页PPT上传
    int card = 2; // 贺卡
    int red_packet = 3; // 有声红包
    int photo = 4; // 快捷讲本
}
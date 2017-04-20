package lib.ys.util.attr;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * android资源里的固定返回为10.0dip相关的字段
 */
@StringDef({
        AttrFloat.layout_width,
        AttrFloat.layout_height,
        AttrFloat.drawable_padding,
        AttrFloat.text_size,
})
@Retention(RetentionPolicy.SOURCE)
public @interface AttrFloat {
    String layout_width = "layout_width";
    String layout_height = "layout_height";
    String drawable_padding = "drawablePadding";
    String text_size = "textSize";
}
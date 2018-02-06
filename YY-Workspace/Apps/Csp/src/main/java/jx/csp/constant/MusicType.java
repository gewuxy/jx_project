package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther HuoXuYu
 * @since 2018/2/3
 */
@IntDef({
        MusicType.theme,
        MusicType.music,
})
@Retention(RetentionPolicy.SOURCE)
public @interface MusicType {
    int theme = 0;  // 获取主题
    int music = 1;  // 获取背景音乐
}

package lib.ys.form;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 根据滑动改变透明度的方式
 *
 * @author yuansui
 */
@IntDef({
        TransparencyType.bg,
        TransparencyType.view,
})
@Retention(RetentionPolicy.SOURCE)
public @interface TransparencyType {
    int bg = 0; // 只有背景改变
    int view = 1; // 整个view改变
}

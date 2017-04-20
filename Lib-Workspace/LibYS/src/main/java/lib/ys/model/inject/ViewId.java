package lib.ys.model.inject;

import android.support.annotation.IdRes;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 替代find view by id
 *
 * @author yuansui
 * @deprecated 实验性功能, 由于性能问题没有解决, 暂时不使用
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface ViewId {
    @IdRes int value() default View.NO_ID;
}

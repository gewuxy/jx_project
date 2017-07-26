package lib.ys.model.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @auther : GuoXuan
 * @since : 2017/7/26
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindArray {
}

package lib.ys.model.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lib.ys.ConstantsEx;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 初始化赋值
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindInit {
    int asInt() default ConstantsEx.KInvalidValue;

    long asLong() default ConstantsEx.KInvalidValue;
}

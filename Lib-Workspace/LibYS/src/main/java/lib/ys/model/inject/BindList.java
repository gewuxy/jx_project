package lib.ys.model.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 申明为列表形式
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindList {
    /**
     * list里保存的对象类
     *
     * @return
     */
    Class value() default void.class;
}
package lib.ys.model.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lib.ys.model.EVal;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 申明为对象形式(目前只支持{@link EVal}的子类)
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindObj {
    /**
     * 对象类
     *
     * @return
     */
    Class<? extends EVal> value() default EVal.class;
}
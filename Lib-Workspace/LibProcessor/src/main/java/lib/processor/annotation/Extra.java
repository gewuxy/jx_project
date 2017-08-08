package lib.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @auther yuansui
 * @since 2017/8/1
 *
 * @see {@link AutoIntent}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Extra {
    /**
     * 替换的key
     *
     * @return
     */
    String value() default "";

    int defaultInt() default 0;

    long defaultLong() default 0l;

    float defaultFloat() default 0f;

    boolean defaultBoolean() default false;

    boolean optional() default false;
}

package inject.annotation.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @auther yuansui
 * @see {@link Route}
 * @since 2017/8/1
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Arg {
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

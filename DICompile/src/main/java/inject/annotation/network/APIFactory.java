package inject.annotation.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface APIFactory {
    /**
     * 正式线
     *
     * @return
     */
    String host();

    /**
     * 测试线
     *
     * @return
     */
    String hostDebuggable() default "";
}

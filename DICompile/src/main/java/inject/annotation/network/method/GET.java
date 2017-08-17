package inject.annotation.network.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface GET {
    String value();
}

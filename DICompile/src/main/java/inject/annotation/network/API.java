package inject.annotation.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * interface专用, 不申明就不会生成对应的api
 *
 * @auther yuansui
 * @since 2017/8/16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface API {
    /**
     * 中间路径
     *
     * @return
     */
    String value() default "";
}

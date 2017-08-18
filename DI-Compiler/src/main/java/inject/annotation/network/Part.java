package inject.annotation.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明参数
 *
 * @auther yuansui
 * @since 2017/8/16
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface Part {
    /**
     * 替换掉默认的key值
     *
     * @return
     */
    String value() default "";

    /**
     * 是否生成链式调用
     *
     * @return
     */
    boolean opt() default false;
}

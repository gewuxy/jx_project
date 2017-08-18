package inject.annotation.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明重试的条件
 *
 * @auther yuansui
 * @since 2017/8/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Retry {
    /**
     * 次数
     *
     * @return
     */
    int count();

    /**
     * 延时
     *
     * @return
     */
    long delay();
}

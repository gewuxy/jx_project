package router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 给Fragment使用的数据传递注释
 * 类成员变量使用{@link Extra}注释
 *
 * @auther yuansui
 * @since 2017/8/1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoArg {
}

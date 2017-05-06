package lib.ys.model.inject.network;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 申明为列表形式
 */
@Target(TYPE)
@Retention(RUNTIME)
@NetworkParser
public @interface AsObj {
}
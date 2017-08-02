package lib.annotation;

/**
 * @auther yuansui
 * @since 2017/8/1
 */

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

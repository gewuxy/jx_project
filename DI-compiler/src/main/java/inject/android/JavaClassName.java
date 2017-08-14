package inject.android;

import com.squareup.javapoet.ClassName;

/**
 * @auther yuansui
 * @since 2017/8/4
 */

public interface JavaClassName {
    ClassName KString = ClassName.get(String.class);
    ClassName KVoid = ClassName.get(Void.class);
    ClassName KBoolean = ClassName.get(Boolean.class);
    ClassName KByte = ClassName.get(Byte.class);
    ClassName KShort = ClassName.get(Short.class);
    ClassName KInt = ClassName.get(Integer.class);
    ClassName KLong = ClassName.get(Long.class);
    ClassName KChar = ClassName.get(CharSequence.class);
    ClassName KFloat = ClassName.get(Float.class);
    ClassName Double = ClassName.get(Double.class);
}

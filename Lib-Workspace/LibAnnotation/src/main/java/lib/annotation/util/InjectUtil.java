package lib.annotation.util;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;

import java.lang.reflect.Method;

import lib.annotation.AutoIntent;

/**
 * @auther yuansui
 * @since 2017/8/2
 */

public class InjectUtil {

    public static void bind(Activity activity) {
        intentBuilder(activity, activity.getIntent());
    }

    public static void bind(Service service, Intent i) {
        intentBuilder(service, i);
    }

    private static void intentBuilder(Object o, Intent i) {
        Class clz = o.getClass();
        if (clz.isAnnotationPresent(AutoIntent.class)) {
            String clsName = clz.getName();
            try {
                Class<?> builderClz = Class.forName(clsName + "Intent");

                Class[] params = new Class[]{clz, Intent.class};
                Method method = builderClz.getMethod("inject", clz, Intent.class);
                method.invoke(null, o, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

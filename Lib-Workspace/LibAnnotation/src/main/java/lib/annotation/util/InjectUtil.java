package lib.annotation.util;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;

import java.lang.reflect.Method;

import lib.annotation.IntentBuilder;

/**
 * @auther yuansui
 * @since 2017/8/2
 */

public class InjectUtil {

    public static void bind(Activity a) {
        intentBuilder(a);
    }

    public static void bind(Service s, Intent i) {
        intentBuilder(s, i);
    }

    private static void intentBuilder(Activity a) {
        Class clz = a.getClass();
        if (clz.isAnnotationPresent(IntentBuilder.class)) {
            String clsName = clz.getName();
            try {
                Class<?> builderClz = Class.forName(clsName + "IntentBuilder");
                Method method = builderClz.getMethod("inject", Activity.class);
                method.invoke(null, a);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void intentBuilder(Service serv, Intent intent) {
        Class clz = serv.getClass();
        if (clz.isAnnotationPresent(IntentBuilder.class)) {
            String clsName = clz.getName();
            try {
                Class<?> builderClz = Class.forName(clsName + "IntentBuilder");
                Method method = builderClz.getMethod("inject", Service.class);
                method.invoke(null, serv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package lib.ys.util;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.lang.reflect.Method;

import inject.annotation.router.Route;


/**
 * @auther yuansui
 * @since 2017/8/2
 */

public class InjectUtil {

    public static void bind(Activity activity) {
        intentBuilder(activity, activity.getIntent());
    }

    public static void bind(Fragment frag) {
        Class clz = frag.getClass();
        if (clz.isAnnotationPresent(Route.class)) {
            String clzName = clz.getName();
            try {
                Class<?> builderClz = Class.forName(clzName + "Router");

                Method method = builderClz.getMethod("inject", clz);
                method.invoke(null, frag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void bind(Service service, Intent i) {
        intentBuilder(service, i);
    }

    private static void intentBuilder(Object o, Intent i) {
        Class clz = o.getClass();
        if (clz.isAnnotationPresent(Route.class)) {
            String clzName = clz.getName();
            try {
                Class<?> builderClz = Class.forName(clzName + "Router");

                Method method = builderClz.getMethod("inject", clz, Intent.class);
                method.invoke(null, o, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

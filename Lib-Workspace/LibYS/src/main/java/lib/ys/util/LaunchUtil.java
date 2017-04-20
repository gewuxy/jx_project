package lib.ys.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import lib.ys.AppEx;

/**
 * PS: host仅限于activity或fragment
 *
 * @author yuansui
 */
public class LaunchUtil {

    public static void startActivity(Context context, Intent intent, Bundle... extras) {
        putExtras(intent, extras);

        if (context instanceof Activity) {
            context.startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void startActivity(Object host, Intent intent, Bundle... extras) {
        putExtras(intent, extras);

        if (host instanceof Activity) {
            ((Activity) host).startActivity(intent);
        } else if (host instanceof Fragment) {
            ((Fragment) host).startActivity(intent);
        }
    }

    public static void startActivity(Context context, Class<?> clz, Bundle... extras) {
        Intent intent = new Intent(context, clz);
        startActivity(context, intent, extras);
    }

    public static void startActivity(Object host, Class<?> clz, Bundle... extras) {
        Intent intent = new Intent(AppEx.ct(), clz);
        startActivity(host, intent, extras);
    }

    public static void startActivityForResult(Object host, Intent intent, int code, Bundle... extras) {
        putExtras(intent, extras);

        if (host instanceof Activity) {
            ((Activity) host).startActivityForResult(intent, code);
        } else if (host instanceof Fragment) {
            ((Fragment) host).startActivityForResult(intent, code);
        }
    }

    public static void startActivityForResult(Object host, Class<?> clz, int code, Bundle... extras) {
        Intent intent = new Intent(AppEx.ct(), clz);
        startActivityForResult(host, intent, code, extras);
    }

    private static void putExtras(Intent intent, Bundle... extras) {
        if (extras != null) {
            for (int i = 0; i < extras.length; ++i) {
                intent.putExtras(extras[i]);
            }
        }
    }
}

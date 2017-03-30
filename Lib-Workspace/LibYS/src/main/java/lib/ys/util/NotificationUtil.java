package lib.ys.util;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import lib.ys.AppEx;

/**
 * @author yuansui
 */
public class NotificationUtil {

    private static NotificationManagerCompat mManager;

    public static Notification launch(int id, NotificationCompat.Builder builder) {
        Notification n = builder.build();
        launch(id, n);
        return n;
    }

    public static void launch(int id, Notification n) {
        getMgr().notify(id, n);
    }

    public static void cancel(int id) {
        getMgr().cancel(id);
    }

    public static void cancelAll() {
        getMgr().cancelAll();
    }

    private static NotificationManagerCompat getMgr() {
        if (mManager == null) {
            mManager = NotificationManagerCompat.from(AppEx.getContext());
        }
        return mManager;
    }
}

package lib.ys;

import android.util.Log;

public final class YSLog {

    private static final String KSeparate = "=========";
    private static boolean mIsDebug = true;

    private YSLog() {
    }

    public static boolean isDebug() {
        return mIsDebug;
    }

    public static void setDebugState(boolean isDebug) {
        mIsDebug = isDebug;
    }

    public static int v(String tag, String msg) {
        if (mIsDebug) {
            return Log.v(tag, msg);
        } else {
            return 0;
        }

    }

    public static int v(String tag, String msg, Throwable tr) {
        if (mIsDebug) {
            return Log.v(tag, msg, tr);
        } else {
            return 0;
        }
    }

    public static int d(String tag, String msg) {
        if (mIsDebug) {
            return Log.d(tag, msg);
        } else {
            return 0;
        }
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (mIsDebug) {
            return Log.d(tag, msg, tr);
        } else {
            return 0;
        }
    }

    public static int i(String tag, String msg) {
        if (mIsDebug) {
            return Log.i(tag, msg);
        } else {
            return 0;
        }
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (mIsDebug) {
            return Log.i(tag, msg, tr);
        } else {
            return 0;
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (mIsDebug) {
            Log.e(tag, msg, tr);
        }
    }

    public static void d(String tag, Throwable e) {
        if (mIsDebug) {
            Log.d(tag, KSeparate + e.getClass().getSimpleName() + KSeparate, e);
        }
    }

    public static void e(String tag, Throwable e) {
        if (mIsDebug) {
            Log.e(tag, KSeparate + e.getClass().getSimpleName() + KSeparate, e);
        }
    }

    public static void e(String tag, String log) {
        if (mIsDebug) {
            Log.e(tag, log);
        }
    }

    public static void w(String tag, String log) {
        if (mIsDebug) {
            Log.w(tag, log);
        }
    }
}

package lib.ys.stats;

import android.content.Context;

import java.util.HashMap;

/**
 * @author yuansui
 */
public class Stats {

    private static boolean mEnableDebug;
    private static IStats mIStats;

    /**
     * @param iStats
     * @param enableDebug 调试模式, 不计入真实统计
     */
    public static void init(IStats iStats, boolean enableDebug) {
        mEnableDebug = enableDebug;
        mIStats = iStats;
    }

    public static void onActivityResume(Context context, String tag) {
        if (mEnableDebug || mIStats == null) {
            return;
        }
        mIStats.onActivityResume(context, tag);
    }

    public static void onActivityPause(Context context, String tag) {
        if (mEnableDebug || mIStats == null) {
            return;
        }
        mIStats.onActivityPause(context, tag);
    }

    public static void onFragmentVisible(String tag) {
        if (mEnableDebug || mIStats == null) {
            return;
        }
        mIStats.onFragmentVisible(tag);
    }

    public static void onFragmentInvisible(String tag) {
        if (mEnableDebug || mIStats == null) {
            return;
        }
        mIStats.onFragmentInvisible(tag);
    }

    public static void onEvent(Context context, String eventId) {
        if (mEnableDebug || mIStats == null) {
            return;
        }
        mIStats.onEvent(context, eventId);
    }

    public static void onEvent(Context context, String eventId, HashMap<String, String> map) {
        if (mEnableDebug || mIStats == null) {
            return;
        }
        mIStats.onEvent(context, eventId, map);
    }
}

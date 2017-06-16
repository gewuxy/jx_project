package lib.jg;

import android.content.Context;

import java.util.HashMap;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import lib.ys.stats.IStats;

/**
 * @author CaiXiang
 * @since 2017/6/16
 */

public class JAnalyticsStats implements IStats {

    @Override
    public void onActivityResume(Context context, String tag) {
        JAnalyticsInterface.onPageStart(context, tag);
    }

    @Override
    public void onActivityPause(Context context, String tag) {
        JAnalyticsInterface.onPageEnd(context, tag);
    }

    @Override
    public void onFragmentVisible(Context context, String tag) {
        JAnalyticsInterface.onPageStart(context, tag);
    }

    @Override
    public void onFragmentInvisible(Context context, String tag) {
        JAnalyticsInterface.onPageEnd(context, tag);
    }

    @Override
    public void onEvent(Context context, String eventId) {
    }

    @Override
    public void onEvent(Context context, String eventId, HashMap<String, String> map) {
    }

}

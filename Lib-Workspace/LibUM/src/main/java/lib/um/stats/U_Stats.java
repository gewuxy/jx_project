package lib.um.stats;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

import java.util.HashMap;

import lib.ys.stats.IStats;


/**
 * @author yuansui
 */
public class U_Stats implements IStats {

    public U_Stats(Context context, String appKey, String channel) {
        UMAnalyticsConfig config = new UMAnalyticsConfig(context, appKey, channel, EScenarioType.E_UM_NORMAL);
        MobclickAgent.startWithConfigure(config);
    }

    public void onActivityResume(Context context, String tag) {
        MobclickAgent.onPageStart(tag);
        MobclickAgent.onResume(context);
    }

    public void onActivityPause(Context context, String tag) {
        MobclickAgent.onPageEnd(tag);
        MobclickAgent.onPause(context);
    }

    @Override
    public void onFragmentVisible(Context context, String tag) {
        MobclickAgent.onPageStart(tag);
    }

    @Override
    public void onFragmentInvisible(Context context, String tag) {
        MobclickAgent.onPageEnd(tag);
    }

    public void onEvent(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    public void onEvent(Context context, String eventId, HashMap<String, String> map) {
        MobclickAgent.onEvent(context, eventId, map);
    }

}

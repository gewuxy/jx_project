package lib.jg;

import android.content.Context;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jiguang.share.android.api.JShareInterface;
import cn.jpush.android.api.JPushInterface;

/**
 * @author CaiXiang
 * @since 2017/6/1
 */

public class JG {

    public static void init(Context context, boolean isDebug) {
        JAnalyticsInterface.setDebugMode(isDebug);
        JAnalyticsInterface.init(context);
        JShareInterface.setDebugModel(isDebug);
        JShareInterface.init(context);
        JPushInterface.setDebugMode(isDebug);
        JPushInterface.init(context);
    }

}

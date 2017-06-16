package lib.ys.stats;

import android.content.Context;

import java.util.HashMap;

/**
 * 统计方法
 *
 * @author yuansui
 */
public interface IStats {

    void onActivityResume(Context context, String tag);

    void onActivityPause(Context context, String tag);

    void onFragmentVisible(Context context, String tag);

    void onFragmentInvisible(Context context, String tag);

    void onEvent(Context context, String eventId);

    void onEvent(Context context, String eventId, HashMap<String, String> map);
}

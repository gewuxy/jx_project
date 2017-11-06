package lib.platform;

import android.content.Context;

import lib.platform.listener.OnAuthListener;
import lib.platform.listener.OnShareListener;
import lib.platform.model.ShareParams;
import lib.platform.provider.MobProvider;
import lib.platform.provider.Provider;

/**
 * @auther yuansui
 * @since 2017/11/6
 */

public class Platform {

    public enum Type {
        qq,
        sina,
        wechat,
        wechat_friend,
        twitter,
        linkedin,
        line,
        qzone,
        sms,
        facebook,
    }

    private static Provider mProvider;

    public static void init(Context context, String key, String secret) {
        if (mProvider == null) {
            mProvider = new MobProvider(context, key, secret);
        }
    }

    public static void auth(Type type, OnAuthListener l) {
        mProvider.auth(type, l);
    }

    public static void share(Type type, ShareParams params, OnShareListener l) {
        mProvider.share(type, params, l);
    }
}

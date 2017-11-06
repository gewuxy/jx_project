package lib.platform.provider;

import lib.platform.Platform.Type;
import lib.platform.listener.OnAuthListener;
import lib.platform.listener.OnShareListener;
import lib.platform.model.ShareParams;

/**
 * @auther yuansui
 * @since 2017/11/6
 */

public interface Provider {
    void init(Type p, String key, String secret);

    void auth(Type type, OnAuthListener l);

    void share(Type type, ShareParams param, OnShareListener l);
}

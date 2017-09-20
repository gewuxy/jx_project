package yaya.csp.util;

import lib.ys.util.CacheUtilEx;

/**
 * Created by yuansui on 2017/3/27.
 */
public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/yaya/csp/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";

    private static String mBmpCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
    }

    public static String getBmpCacheDir() {
        return mBmpCacheDir;
    }
}

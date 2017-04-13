package yy.doctor.util;

import lib.ys.util.CacheUtilEx;

/**
 * Created by Administrator on 2017/3/27.
 */

public class CacheUtil extends CacheUtilEx {
    private static final String KHomeDir = "/yaya/";

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

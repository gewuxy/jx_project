package yy.doctor.util;

import lib.ys.util.CacheUtilEx;

/**
 * Created by Administrator on 2017/3/27.
 */

public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/yaya/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";

    private static final String KFileCacheDef = KHomeDir + "cache/file/";

    private static String mBmpCacheDir;
    private static String mFileCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mFileCacheDir = makeDir(KFileCacheDef);
    }

    public static String getBmpCacheDir() {
        return mBmpCacheDir;
    }

    public static String getFileCacheDir() {
        return mFileCacheDir;
    }

}

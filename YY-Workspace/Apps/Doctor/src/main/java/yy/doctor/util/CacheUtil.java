package yy.doctor.util;

import lib.ys.util.CacheUtilEx;

/**
 * Created by Administrator on 2017/3/27.
 */

public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/yaya/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";

    private static final String KDownloadCacheDef = KHomeDir + "cache/download/";

    private static String mBmpCacheDir;
    private static String mDownloadCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mDownloadCacheDir = makeDir(KDownloadCacheDef);
    }

    public static String getBmpCacheDir() {
        return mBmpCacheDir;
    }

    public static String getDownloadCacheDir() {
        return mDownloadCacheDir;
    }

}

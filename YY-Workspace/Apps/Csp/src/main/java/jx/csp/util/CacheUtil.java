package jx.csp.util;

import lib.ys.util.CacheUtilEx;

/**
 * Created by yuansui on 2017/3/27.
 */
public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/yaya/csp/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";
    private static final String KUploadCacheDef = KHomeDir + "upload/tmp/";
    private static final String KDownloadCacheDef = KHomeDir + "cache/download/";
    private static final String KMeetingCacheDef = KDownloadCacheDef + "meeting/";

    private static String mBmpCacheDir;
    private static String mUploadCacheDir;
    private static String mMeetingCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mUploadCacheDir = makeDir(KUploadCacheDef);
        mMeetingCacheDir = makeDir(KMeetingCacheDef);
    }

    public static String getBmpCacheDir() {
        return mBmpCacheDir;
    }

    public static String getUploadCacheDir() {
        return mUploadCacheDir;
    }

    public static String getMeetingSoundCacheDir() {
        return mMeetingCacheDir;
    }

}

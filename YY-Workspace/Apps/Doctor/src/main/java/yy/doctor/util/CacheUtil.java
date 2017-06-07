package yy.doctor.util;

import java.io.File;

import lib.ys.util.CacheUtilEx;

/**
 * Created by yuansui on 2017/3/27.
 */
public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/yaya/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";

    private static final String KDownloadCacheDef = KHomeDir + "cache/download/";
    private static final String KUnitNumCacheDef = KDownloadCacheDef + "unit_num/";
    private static final String KMeetingCacheDef = KDownloadCacheDef + "meeting/";


    private static String mBmpCacheDir;
    private static String mDownloadCacheDir;
    private static String mUnitNumCacheDir;
    private static String mMeetingCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mDownloadCacheDir = makeDir(KDownloadCacheDef);
        mUnitNumCacheDir = makeDir(KUnitNumCacheDef);
        mMeetingCacheDir = makeDir(KMeetingCacheDef);
    }

    public static String getBmpCacheDir() {
        return mBmpCacheDir;
    }

    public static String getDownloadCacheDir() {
        return mDownloadCacheDir;
    }

    public static String getUnitNumCacheDir(int id) {
        return mUnitNumCacheDir + String.valueOf(id) + File.separator;
    }

    public static File getUnitNumCacheFile(int id, String fileName) {
        File destFile = new File(getUnitNumCacheDir(id) + fileName);
        return destFile;
    }

    public static String getMeetingCacheDir(String id) {
        return mMeetingCacheDir + id + File.separator;
    }

    public static File getMeetingCacheFile(String id, String fileName) {
        File destFile = new File(getMeetingCacheDir(id) + fileName);
        return destFile;
    }
}

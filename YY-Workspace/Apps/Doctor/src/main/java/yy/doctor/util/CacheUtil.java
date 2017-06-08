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
    private static final String KThomsonCacheDef = KDownloadCacheDef + "thomson/";
    private static final String KMeetingCacheDef = KDownloadCacheDef + "meeting/";


    private static String mBmpCacheDir;
    private static String mDownloadCacheDir;
    private static String mUnitNumCacheDir;
    private static String mThomsonCacheDir;
    private static String mMeetingCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mDownloadCacheDir = makeDir(KDownloadCacheDef);
        mUnitNumCacheDir = makeDir(KUnitNumCacheDef);
        mThomsonCacheDir = makeDir(KThomsonCacheDef);
        mMeetingCacheDir = makeDir(KMeetingCacheDef);
    }

    public static String getBmpCacheDir() {
        return mBmpCacheDir;
    }

    public static String getDownloadCacheDir() {
        return mDownloadCacheDir;
    }

    public static String getUnitNumCacheDir(String id) {
        return mUnitNumCacheDir + id + File.separator;
    }

    public static File getUnitNumCacheFile(String id, String fileName) {
        File destFile = new File(getUnitNumCacheDir(id) + fileName);
        return destFile;
    }

    public static String getThomsonCacheDir(String categoryId) {
        return mThomsonCacheDir + categoryId + File.separator;
    }

    public static File getThomsonCacheFile(String categoryId, String fileName) {
        File destFile = new File(getThomsonCacheDir(categoryId) + fileName);
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

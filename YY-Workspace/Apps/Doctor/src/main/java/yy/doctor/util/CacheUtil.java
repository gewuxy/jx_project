package yy.doctor.util;

import java.io.File;

import lib.ys.util.CacheUtilEx;

/**
 * Created by yuansui on 2017/3/27.
 */
public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/yaya/doctor/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";

    private static final String KDownloadCacheDef = KHomeDir + "cache/download/";
    private static final String KUnitNumCacheDef = KDownloadCacheDef + "unit_num/";
    private static final String KThomsonCacheDef = KDownloadCacheDef + "thomson".hashCode() + "/";
    private static final String KMeetingCacheDef = KDownloadCacheDef + "meeting/";
    private static final String KUploadCacheDef = KHomeDir + "upload/tmp/";

    private static String mBmpCacheDir;
    private static String mDownloadCacheDir;
    private static String mUnitNumCacheDir;
    private static String mThomsonCacheDir;
    private static String mMeetingCacheDir;
    private static String mUploadCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mDownloadCacheDir = makeDir(KDownloadCacheDef);
        mUnitNumCacheDir = makeDir(KUnitNumCacheDef);
        mThomsonCacheDir = makeDir(KThomsonCacheDef);
        mMeetingCacheDir = makeDir(KMeetingCacheDef);
        mUploadCacheDir = makeDir(KUploadCacheDef);
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

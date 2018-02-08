package jx.csp.util;

import java.io.File;

import lib.ys.model.FileSuffix;
import lib.ys.util.CacheUtilEx;
import lib.ys.util.FileUtil;

/**
 * Created by yuansui on 2017/3/27.
 */
public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/jx/csp/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";
    private static final String KUploadCacheDef = KHomeDir + "upload/tmp/";
    private static final String KAudioCacheDef = KHomeDir + "audio/";
    private static final String KVideoCacheDef = KHomeDir + "video/";
    private static final String KApkCacheDef = KHomeDir + "apk/";
    private static final String KBgMusicCacheDef = KHomeDir + "bgMusic/";

    private static String mBmpCacheDir;
    private static String mUploadCacheDir;
    private static String mAudioCacheDir;
    private static String mVideoCacheDir;
    private static String mApkCacheDir;
    private static String mBgMusicCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mUploadCacheDir = makeDir(KUploadCacheDef);
        mAudioCacheDir = makeDir(KAudioCacheDef);
        mVideoCacheDir = makeDir(KVideoCacheDef);
        mApkCacheDir = makeDir(KApkCacheDef);
        mBgMusicCacheDir = makeDir(KBgMusicCacheDef);
    }

    public static String getBmpCacheDir() {
        return mBmpCacheDir;
    }

    public static String getUploadCacheDir() {
        return mUploadCacheDir;
    }

    public static String getAudioCacheDir() {
        return mAudioCacheDir;
    }

    public static String getVideoCacheDir() {
        return mVideoCacheDir;
    }

    public static String getApkCacheDir() {
        return mApkCacheDir;
    }

    public static String getBgMusicCacheDir() {
        return mBgMusicCacheDir;
    }

    public static String createAudioFile(String courseId, int pageId) {
        String basePath = mAudioCacheDir + courseId + File.separator + pageId + File.separator;
        FileUtil.ensureFileExist(basePath);
        // 先判断1.amr 文件是否存在， 如果存在就用2.amr 以此类推
        int fileName = 1;
        for (int i = 1; i < 6; ++i) {
            File file = new File(basePath + i + FileSuffix.amr);
            if (!file.exists()) {
                fileName = i;
                break;
            }
        }
        return basePath + fileName + FileSuffix.amr;
    }

    public static String getExistAudioFilePath(String courseId, int pageId) {
        String basePath = mAudioCacheDir + courseId + File.separator + pageId;
        FileUtil.ensureFileExist(basePath);
        File folder = new File(basePath);
        File[] files = folder.listFiles();
        if (files.length == 0) {
            return basePath + File.separator + 1 + FileSuffix.amr;
        } else {
            return (folder.listFiles())[0].getAbsolutePath();
        }
    }

    public static String getBgMusicFilePath(String id) {
        return mBgMusicCacheDir + File.separator + id + FileSuffix.mp3;
    }

}

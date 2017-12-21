package jx.csp.util;

import java.io.File;

import lib.ys.model.FileSuffix;
import lib.ys.util.CacheUtilEx;

/**
 * Created by yuansui on 2017/3/27.
 */
public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/jx/csp/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";
    private static final String KUploadCacheDef = KHomeDir + "upload/tmp/";
    private static final String KAudioCacheDef = KHomeDir + "audio/";
    private static final String KVideoCacheDef = KHomeDir + "video/";

    private static final String KVideoFileNameLogin = "login";

    private static String mBmpCacheDir;
    private static String mUploadCacheDir;
    private static String mAudioCacheDir;
    private static String mVideoCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mUploadCacheDir = makeDir(KUploadCacheDef);
        mAudioCacheDir = makeDir(KAudioCacheDef);
        mVideoCacheDir = makeDir(KVideoCacheDef);
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

    public static String getAudioPath(String courseId, int pageId) {
        return mAudioCacheDir + courseId + File.separator + pageId + FileSuffix.amr;
    }

    public static String getVideoLoginFileName() {
        return KVideoFileNameLogin + FileSuffix.mp4;
    }
}

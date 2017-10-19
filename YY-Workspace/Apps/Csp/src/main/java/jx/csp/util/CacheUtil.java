package jx.csp.util;

import java.io.File;

import lib.ys.model.FileSuffix;
import lib.ys.util.CacheUtilEx;

/**
 * Created by yuansui on 2017/3/27.
 */
public class CacheUtil extends CacheUtilEx {

    private static final String KHomeDir = "/yaya/csp/";

    private static final String KBmpCacheDef = KHomeDir + "cache/bmp/";
    private static final String KUploadCacheDef = KHomeDir + "upload/tmp/";
    private static final String KAudioCacheDef = KHomeDir + "audio/";

    private static String mBmpCacheDir;
    private static String mUploadCacheDir;
    private static String mAudioCacheDir;

    static {
        init(KHomeDir);

        mBmpCacheDir = makeDir(KBmpCacheDef);
        mUploadCacheDir = makeDir(KUploadCacheDef);
        mAudioCacheDir = makeDir(KAudioCacheDef);
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

    public static String getAudioPath(String courseId, int page) {
        // fixme: 暂时page+1
        return mAudioCacheDir + courseId + File.separator + (page + 1) + FileSuffix.amr;
    }
}

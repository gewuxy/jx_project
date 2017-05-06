package lib.ys.util;

import java.io.File;

import lib.ys.AppEx;

abstract public class CacheUtilEx {

    public static final String KTxtExtend = ".txt";
    public static final String KXmlExtend = ".xml";
    public static final String KHtmlExtend = ".html";
    public static final String KJpgExtend = ".jpg";
    public static final String KPngExtend = ".png";
    public static final String KJsExtend = ".js";

    private static final String KNoMediaFileName = ".nomedia";

    private static String mBasePath;

    protected static void init(String homeDir) {
        makeDir(homeDir + KNoMediaFileName);
    }

    protected static String makeDir(String dir) {
        String newPath = getBasePath() + dir;
        ensureFileExist(newPath);
        return newPath;
    }

    protected static boolean ensureFileExist(String filePath) {
        return FileUtil.ensureFileExist(filePath);
    }

    protected static boolean ensureFileExist(File file) {
        return FileUtil.ensureFileExist(file);
    }

    protected static String getBasePath() {
        // 兼容6.0的文件动态权限问题, 尽量不进行申请, 某些机型申请不过
        if (mBasePath == null) {
            File diskRootFile;
            if (DeviceUtil.isSdcardEnable()) {
                diskRootFile = AppEx.ct().getExternalCacheDir();
            } else {
                diskRootFile = AppEx.ct().getCacheDir();
            }

            if (diskRootFile != null) {
                mBasePath = diskRootFile.getPath();
            } else {
                throw new IllegalArgumentException("disk is invalid");
            }
        }
        return mBasePath;
    }
}

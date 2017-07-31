package lib.ys.util;

import java.io.File;

import lib.ys.AppEx;

abstract public class CacheUtilEx {

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

            // FIXME: 某些机型在sdcard enable的情况下并不能获取到ExternalCacheDir
            if (diskRootFile == null) {
                diskRootFile = DeviceUtil.getSdcardDir();
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

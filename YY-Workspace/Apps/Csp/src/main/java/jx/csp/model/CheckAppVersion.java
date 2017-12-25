package jx.csp.model;

import jx.csp.model.CheckAppVersion.TCheckAppVersion;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2017/12/25
 */

public class CheckAppVersion extends EVal<TCheckAppVersion> {

    public enum TCheckAppVersion{
        version,  // 版本号 100
        versionStr,  // 版本号字符串格式 1.0.0
        driveTag,  // app驱动类型 例如 android
        downLoadUrl,  // 更新下载地址
        fileSize,  // 更新包大小
        appType,  // 应用类型 例如 cspmeeting_cn
        forced,  // 是否强制更新
    }
}

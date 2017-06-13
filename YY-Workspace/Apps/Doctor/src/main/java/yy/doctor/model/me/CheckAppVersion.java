package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.CheckAppVersion.TCheckAppVersion;

/**
 * @author CaiXiang
 * @since 2017/6/13
 */

public class CheckAppVersion extends EVal<TCheckAppVersion> {

    public enum TCheckAppVersion {
        appType,
        details,
        downLoadUrl,
        driveTag,
        fileSize,
        id,
        updateTime,
        version,
    }

}

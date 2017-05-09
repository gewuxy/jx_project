package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.InfoModules.TInfoModules;

/**
 * 会议包含的模块
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class InfoModules extends EVal<TInfoModules> {
    public enum TInfoModules {
        active,
        functionId,//模块功能ID
        id,
        meetId,
        moduleName,
    }
}

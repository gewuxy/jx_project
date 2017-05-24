package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.Module.TModule;

/**
 * 会议包含的模块
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class Module extends EVal<TModule> {
    public enum TModule {
        active,
        functionId,//模块功能ID
        id,
        meetId,
        moduleName,
    }
}

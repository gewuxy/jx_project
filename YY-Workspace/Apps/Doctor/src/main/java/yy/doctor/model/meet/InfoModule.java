package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.InfoModule.TInfoModule;

/**
 * 会议包含的模块
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class InfoModule extends EVal<TInfoModule> {
    public enum TInfoModule {
        active,
        functionId,//模块功能ID
        id,
        meetId,
        moduleName,
    }
}

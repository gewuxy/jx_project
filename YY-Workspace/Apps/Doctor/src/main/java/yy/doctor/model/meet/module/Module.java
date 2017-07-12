package yy.doctor.model.meet.module;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import yy.doctor.model.meet.module.Module.TModule;

/**
 * 会议包含的模块
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class Module extends EVal<TModule> {

    @IntDef({
            ModuleType.ppt,
            ModuleType.video,
            ModuleType.exam,
            ModuleType.que,
            ModuleType.sign,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ModuleType {
        int ppt = 1; // 微课
        int video = 2; // 视频
        int exam = 3; // 考试
        int que = 4; // 问卷
        int sign = 5; // 签到
    }

    public enum TModule {
        active,

        /**
         * {@link ModuleType}
         */
        functionId,//模块功能ID

        id,
        meetId,
        moduleName,
    }
}

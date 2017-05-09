package yy.doctor.model.meet;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.meet.DetailInfo.TDetailInfo;

/**
 * 会议详情
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class DetailInfo extends EVal<TDetailInfo> {
    public enum TDetailInfo {
        eduCredits,
        id,
        lecturer,//主讲者
        meetName,//会议名称
        meetType,//会议科室类型
        @BindList(InfoModules.class)
        modules,//会议包含的模块
        organizer,//会议主办方
        requiredXs,
        startTime,
        state,//会议状态
        xsCredits,
    }
}

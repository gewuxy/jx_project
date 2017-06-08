package yy.doctor.model.meet;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;

/**
 * 会议详情
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class MeetDetail extends EVal<TMeetDetail> {
    public enum TMeetDetail {
        id,
        lecturer, // 主讲者

        meetName, // 会议名称
        meetType, // 会议科室类型

        @BindList(Module.class)
        modules, // 会议包含的模块

        stored, // 是否收藏
        organizer, // 会议主办方
        eduCredits, // 是否需要象数
        requiredXs, // 是否支付过象数
        startTime, // 开始时间
        endTime, // 结束时间
        state, // 会议状态
        xsCredits,
        introduction, // 简介
    }
}

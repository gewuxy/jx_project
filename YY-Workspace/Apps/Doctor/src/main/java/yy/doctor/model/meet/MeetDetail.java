package yy.doctor.model.meet;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.unitnum.FileData;

/**
 * 会议详情
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class MeetDetail extends EVal<TMeetDetail> {
    public enum TMeetDetail {
        id,
        state, // 会议状态
        stored, // 是否收藏
        attention, // 是否关注了

        coverUrl, // 缩略图
        startTime, // 开始时间
        endTime, // 结束时间

        meetName, // 会议名称
        meetType, // 会议科室类型

        headimg, // 单位号头像
        pubUserId, // 公众号Id
        organizer, // 会议主办方

        lecturer, // 主讲者
        lecturerHead, // 头像
        lecturerHos, // 医院
        lecturerTitle, // 职责

        eduCredits, // 是否需要象数，0需要,1奖励
        remainAward, // 剩余奖励人数
        requiredXs, // 是否支付过象数
        attended, // 参加过(奖励过和支付过)
        xsCredits, // 象数

        attendAble, // 能否参加会议
        reason, // 不能参加会议的理由

        materialCount,  //  资料数

        @BindList(FileData.class)
        materials, // 资料

        @BindList(Module.class)
        modules, // 会议包含的模块

        introduction, // 简介
    }
}

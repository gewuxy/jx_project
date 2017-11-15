package yy.doctor.model.meet;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.model.meet.module.Module;
import yy.doctor.model.unitnum.File;

/**
 * 会议详情
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class MeetDetail extends EVal<TMeetDetail> {

    @IntDef({
            CollectType.cancel,
            CollectType.collect,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface CollectType {
        int cancel = 0; // 收藏
        int collect = 1; // 没有收藏
    }

    @IntDef({
            BroadcastType.reb,
            BroadcastType.live_ppt,
            BroadcastType.live,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BroadcastType {
        int reb = 0; // 录播
        int live_ppt = 1; // ppt直播
        int live = 2; // ppt直播 + 视频
    }

    public enum TMeetDetail {
        id,

        /**
         * {@link MeetState}
         */
        state, // 会议状态

        /**
         * {@link CollectType}
         */
        stored, // 是否收藏
        attention, // 是否关注了

        coverUrl, // 缩略图
        startTime, // 开始时间
        endTime, // 结束时间

        meetName, // 会议名称
        meetType, // 会议科室类型

        headimg, // 单位号头像
        pubUserId, // 单位号Id
        organizer, // 会议主办方

        lecturer, // 主讲者
        lecturerHead, // 头像
        lecturerHos, // 医院
        lecturerTitle, // 职责

        attended, // 参加过(支付过)
        attendAble, // 能否参加会议

        /**
         * {@link BroadcastType}
         */
        playType, // 会议直播类型

        requiredXs, // 是否奖励象数  true表示奖励；当为false表示支付(且xsCredits大于0时)
        xsCredits, // 奖励/支付象数
        remainAwardXsCount, // 剩余奖励象数人数
        receiveAwardXs, // 获得奖励象数(已经)

        rewardCredit, // 是否奖励学分  true表示奖励 false表示不奖励
        eduCredits, // 奖励学分  当会议有奖励学分的时候才会有值
        remainAwardCreditCount, // 剩余奖励学分人数
        receiveAwardCredit, // 获得奖励学分(已经)

        completeProgress, // 学习进度

        reason, // 不能参加会议的理由

        materialCount,  //  资料数

        @Bind(asList = File.class)
        materials, // 资料

        @Bind(asList = Module.class)
        modules, // 会议包含的模块

        introduction, // 简介

        reprintFromUnitUser, // 会议转载来自哪个单位号

        /**
         * 无用
         */
        awardLimit,     // 奖励象数限制人数
        awardCreditLimit, // 奖励学分限制人数
    }

    public String getBroadcastType() {
        if (getInt(TMeetDetail.playType, 0) == BroadcastType.reb) {
            return "录播";
        } else {
            return "直播";
        }

    }
}

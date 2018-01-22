package jx.csp.model.main;

import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import lib.ys.model.EVal;

/**
 * 首页会议
 *
 * @auther WangLan
 * @since 2017/10/18
 */
public class Meet extends EVal<TMeet> {

    public enum TMeet {
        coverUrl, // 封面地址
        id, // 会议id
        livePage, // 直播，当前ppt页码

        /**
         * {@link LiveState}
         */
        liveState, // 直播状态

        pageCount, // ppt总页数
        playTime, // 播放时长

        /**
         * {@link  CourseType}
         */
        playType, // 播放类型，0表示录播，1表示PPT直播，2表示视频直播

        startTime, // 开始时间
        endTime, // 结束时间
        title, // 会议标题
        playPage, // 录播，当前ppt页码

        /**
         * {@link PlayState}
         */
        playState, // 录播状态
        serverTime, // 服务器当前时间

        starRateFlag,// true 是否开启星评
        info,   //会议简介
    }
}

package jx.csp.model.main;

import jx.csp.constant.FiltrateType;
import jx.csp.constant.SourceType;
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

        starRateFlag, // true 是否开启星评
        password,  // 会议观看密码

        /**
         * {@link SourceType}
         */
        sourceType, // 课件来源
    }

    @FiltrateType
    public int getType() {
        @FiltrateType int type = FiltrateType.ppt;
        switch (getInt(TMeet.sourceType)) {
            case SourceType.yaya:
            case SourceType.ppt:
            case SourceType.card:
            case SourceType.red_packet: {
                type = FiltrateType.ppt;
            }
            break;
            case SourceType.photo: {
                type = FiltrateType.photo;
            }
            break;
        }
        return type;
    }
}

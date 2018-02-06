package jx.csp.model.meeting;

import jx.csp.model.meeting.JoinMeeting.TJoinMeeting;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2017/10/18
 */

public class JoinMeeting extends EVal<TJoinMeeting> {

    public enum TJoinMeeting {

        @Bind(Course.class)
        course, // 	课件基本信息

        @Bind(Live.class)
        live, // 直播信息

        @Bind(Record.class)
        record, // 录播信息

        @Bind(BgMusicThemeInfo.class)
        theme, // 讲本皮肤和背景音乐对象 未设置则为null

        wsUrl, // 直播或者同步ws地址
        serverTime, // 服务器当前时间
    }
}

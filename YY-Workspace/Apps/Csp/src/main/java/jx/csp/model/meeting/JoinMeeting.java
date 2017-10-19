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

        wsUrl, // 直播或者同步ws地址
    }
}

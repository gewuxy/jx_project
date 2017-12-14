package jx.csp.model.meeting;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.model.meeting.Scan.TScan;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2017/10/20
 */

public class Scan extends EVal<TScan> {

    @IntDef({
            DuplicateType.no,
            DuplicateType.yes,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DuplicateType {
        int no = 0;
        int yes = 1;
    }

    public enum TScan {
        courseId,

        /**
         * {@link Course.PlayType}
         */
        playType,
        duplicate,  // 	是否有重复登录  0表示没有 1表示有
        wsUrl, //  websocket地址 duplicate=1时才有 视频直播的时候都有
        startTime, // 开始时间
        endTime, // 结束时间
        title, // 会议标题
        coverUrl, // 封面地址
        serverTime,  //	服务器当前时间
        pushUrl, // 推流地址
    }
}

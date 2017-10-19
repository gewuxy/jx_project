package jx.csp.model.meeting;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.model.meeting.Live.TLive;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2017/10/18
 */

public class Live extends EVal<TLive> {

    @IntDef({
            LiveState.un_start,
            LiveState.live,
            LiveState.end
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface LiveState {
        int un_start = 0; // 未开始
        int live = 1; // 正在直播中
        int end = 2; // 已结束
    }

    public enum TLive {
        courseId, // 	课件id
        startTime, // 开始时间
        endTime, // 结束时间

        /**
         * {@link LiveState}
         */
        liveState // 直播状态
    }
}

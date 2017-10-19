package jx.csp.model.meeting;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.model.meeting.Record.TRecord;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2017/10/18
 */

public class Record extends EVal<TRecord> {

    @IntDef({
            PlayState.un_start,
            PlayState.record,
            PlayState.end
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayState {
        int un_start = 0; // 未开始
        int record = 1; // 正在录播中
        int end = 2; // 已结束
    }

    public enum TRecord {
        courseId, // 课件ID
        playPage, // 当前录播页码

        /**
         * {@link PlayState}
         */
        playState, // 录播状态
    }
}

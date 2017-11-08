package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 会议状态
 */
@IntDef({
        MeetState.other,
        MeetState.living,
        MeetState.playing,

})
@Retention(RetentionPolicy.SOURCE)
public @interface MeetState {
    int other = 0; // 其他状态
    int living = 1; // 直播中
    int playing = 2; // 录播中
}
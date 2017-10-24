package jx.csp.model.meeting;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.model.meeting.Course.TCourse;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2017/10/18
 */

public class Course extends EVal<TCourse> {

    //播放类型
    @IntDef({
            PlayType.reb,
            PlayType.live,
            PlayType.video
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayType {
        int reb = 0; // 录播
        int live = 1; // ppt直播
        int video = 2; // 视频直播
    }


    public enum TCourse {
        id, // 课件ID
        title, // 课件标题

        /**
         * {@link  PlayType}
         */
        playType, // 播放类型

        @Bind(asList = CourseDetail.class)
        details,
    }
}

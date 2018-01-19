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
            CourseType.reb,
            CourseType.ppt_live,
            CourseType.ppt_video_live
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface CourseType {
        int reb = 0; // 录播
        int ppt_live = 1; // ppt直播
        int ppt_video_live = 2; // ppt + 视频直播
    }

    public enum TCourse {
        id, // 课件ID
        title, // 课件标题

        /**
         * {@link  CourseType}
         */
        playType, // 播放类型

        @Bind(asList = CourseDetail.class)
        details,
    }
}

package jx.doctor.model.meet.video;

import lib.ys.model.EVal;
import jx.doctor.model.meet.video.Intro.TIntro;

/**
 * 视频课程全部信息
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class Intro extends EVal<TIntro> {

    public enum TIntro {

        @Bind(Course.class)
        course,//视频课程信息

        id,//课程id
        meetId,//会议id
        moduleId,//模块id
    }
}

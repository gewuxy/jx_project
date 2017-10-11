package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.PPT.TPPT;

/**
 * 微课信息
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class PPT extends EVal<TPPT> {

    public enum TPPT {
        @Bind(CourseInfo.class)
        course, // 微课

        courseId, // 微课ID
        id, // 微课明细ID
        meetId, // 会议ID
        moduleId, // 模块ID
        count, // 会议评论人数
    }
}

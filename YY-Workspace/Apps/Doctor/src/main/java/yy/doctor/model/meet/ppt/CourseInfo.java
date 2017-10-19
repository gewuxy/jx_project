package yy.doctor.model.meet.ppt;

import lib.ys.model.EVal;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;

/**
 * 微课概要明细信息
 *
 * @author : GuoXuan
 * @since : 2017/5/9
 */
public class CourseInfo extends EVal<TCourseInfo> {

    public enum TCourseInfo {
        category,//微课类型(科室)

        @Bind(asList = Course.class)
        details,//微课列表

        title,//微课名称
    }
}

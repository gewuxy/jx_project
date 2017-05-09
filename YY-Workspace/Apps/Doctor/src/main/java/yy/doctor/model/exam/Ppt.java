package yy.doctor.model.exam;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindObj;
import yy.doctor.model.exam.Ppt.TPpt;

/**
 * 微课信息
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Ppt extends EVal<TPpt> {
    public enum TPpt {
        @BindObj(Course.class)
        course,//微课
        courseId,//微课ID
        id,//微课明细ID
        meetId,//会议ID
        moduleID,//模块ID
    }
}

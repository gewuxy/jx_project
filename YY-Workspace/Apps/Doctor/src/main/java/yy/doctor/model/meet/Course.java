package yy.doctor.model.meet;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.meet.Course.TCourse;

/**
 * 微课概要明细信息
 *
 * @author : GuoXuan
 * @since : 2017/5/9
 */

public class Course extends EVal<TCourse> {
    public enum TCourse {
        category,//微课类型(科室)
        @BindList(Details.class)
        details,//微课明细
        title,//微课名称
    }
}

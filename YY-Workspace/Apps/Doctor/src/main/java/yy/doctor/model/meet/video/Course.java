package yy.doctor.model.meet.video;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.meet.video.Course.TCourse;

/**
 * 视频课程信息
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class Course extends EVal<TCourse> {
    public enum TCourse {

        @BindList(Detail.class)
        details, // 课程明细

        id, // 明细id
        title, // 课程名称
    }
}

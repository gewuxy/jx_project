package jx.csp.model.meeting;

import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2017/10/18
 */

public class CourseDetail extends EVal<TCourseDetail> {

    public enum TCourseDetail {
        id, // ppt的id  每页都有对应的id
        sort,
        duration, // 音频时间长度 单位秒
        audioUrl,
        imgUrl,
        videoUrl
    }
}

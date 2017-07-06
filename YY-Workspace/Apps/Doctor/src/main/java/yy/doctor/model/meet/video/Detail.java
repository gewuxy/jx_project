package yy.doctor.model.meet.video;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindInit;
import yy.doctor.model.meet.video.Detail.TDetail;

/**
 * 视频课程明细
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class Detail extends EVal<TDetail> {
    public enum TDetail {
        courseId, // 所属课程ID
        id, // 明细id
        name, // 明细名称
        preId, // 明细上级目录ID
        type, // 明细类型
        duration, // 视频时长
        url, // 视频URL
        videoType, // 视频连接类型

        @BindInit(asLong = 0)
        userdtime, // 已学习时间
    }
}

package yy.doctor.model.meet.video;

import lib.ys.model.EVal;
import yy.doctor.model.meet.video.Detail.TDetail;

/**
 * 视频课程明细
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class Detail extends EVal<TDetail> {
    public enum TDetail {
        courseId,//所属课程ID
        duration,//
        id,//明细id
        name,//明细名称
        preId,//明细上级目录ID
        type,//明细类型
        url,//视频时长
        videoType,//视频连接类型
    }
}

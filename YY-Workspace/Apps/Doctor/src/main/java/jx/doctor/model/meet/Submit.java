package jx.doctor.model.meet;

import lib.ys.model.EVal;
import jx.doctor.model.meet.Submit.TSubmit;

/**
 * 会议视频专用(都是本地字段)
 *
 * @auther : GuoXuan
 * @since : 2017/6/20
 */
public class Submit extends EVal<TSubmit> {
    public enum TSubmit {
        meetId, // 会议ID
        moduleId, // 模块ID
        courseId, // 视频课程ID
        detailId, // 视频明细ID
        usedtime, // 明细用时
        finished, // 是否完成
        times, // (视频明细ID,明细用时,是否完成)的集合
    }
}

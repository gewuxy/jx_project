package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.Detail.TDetail;

/**
 * 微课具体明细信息
 *
 * @author : GuoXuan
 * @since : 2017/5/9
 */

public class Detail extends EVal<TDetail> {
    public enum TDetail {
        audioUrl, // 微课音频明细路径
        id, // 微课明细ID
        imgUrl, // 微课图片明细路径
        sort, // 微课明细序号
        videoUrl, // 微课视频明细路径
    }
}

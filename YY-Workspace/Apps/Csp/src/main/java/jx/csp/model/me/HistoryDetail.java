package jx.csp.model.me;

import jx.csp.model.me.HistoryDetail.THistoryDetail;
import lib.ys.model.EVal;

/**
 * @auther WangLan
 * @since 2017/10/13
 */

public class HistoryDetail extends EVal<THistoryDetail> {

    public enum THistoryDetail {
        id, // 投稿id
        title, // 课件标题
        playType, // 播放模式 0录播 1ppt直播 2视频直播
        coverUrl, // 封面地址
        startTime, // 开始时间 直播才有，下同
        endTime, // 结束时间
        duration, // 录播总时长
        pageCount, // 总页数
    }
}

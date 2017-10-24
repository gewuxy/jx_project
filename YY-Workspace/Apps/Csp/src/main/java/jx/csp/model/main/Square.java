package jx.csp.model.main;

import jx.csp.model.main.Square.TSquare;
import lib.ys.model.EVal;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class Square extends EVal<TSquare> {
    public enum TSquare{
        coverUrl, // 封面地址
        id, // 会议id
        livePage, // 直播，当前ppt页码
        liveState, // 直播状态
        pageCount, // ppt总页数
        playTime, // 播放时长
        playType, // 播放类型，0表示录播，1表示PPT直播，2表示视频直播
        startTime, // 开始时间
        endTime, // 结束时间
        title, // 会议标题
        playPage, // 录播，当前ppt页码
        playState, // 录播状态

    }

}

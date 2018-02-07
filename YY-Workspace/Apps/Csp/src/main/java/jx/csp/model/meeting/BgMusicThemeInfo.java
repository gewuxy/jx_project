package jx.csp.model.meeting;

import jx.csp.model.meeting.BgMusicThemeInfo.TBgMusicThemeInfo;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/2/6
 */

public class BgMusicThemeInfo extends EVal<TBgMusicThemeInfo> {

    public enum TBgMusicThemeInfo {
        id,  //	皮肤ID
        courseId,  // 讲本ID
        imgName,  // 背景图名称
        imgUrl,  //	背景图地址
        name,   // 背景音名称
        url,  // 背景音地址
        musicId,
        duration,  // 背景音时长
    }
}

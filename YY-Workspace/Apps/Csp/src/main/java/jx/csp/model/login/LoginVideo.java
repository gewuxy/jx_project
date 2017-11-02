package jx.csp.model.login;

import jx.csp.model.login.LoginVideo.TLoginVideo;
import lib.ys.model.EVal;

/**
 * @auther WangLan
 * @since 2017/9/26
 */

public class LoginVideo extends EVal<TLoginVideo>{
    public enum TLoginVideo {
        id, // 视频记录Id
        version, // 版本号
        createTime, // 视频更新时间
        videoUrl, // 下载视频url
    }
}

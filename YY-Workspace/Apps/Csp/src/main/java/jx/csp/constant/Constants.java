package jx.csp.constant;

import lib.jx.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public interface Constants extends BaseConstants {

    int KAccountFrozen = 103;

    String KVideoUrl = "http://139.199.170.178/cspvideo/login_background_video.mp4";

    interface PageConstants {
        int KPage = 1;  // 起始页页数
        int KPageSize = 20;  //分页加载时 每页加载的条目数
    }

    String KAppTypeCn = "cn";
    String KData = "data";
}

package jx.csp.model;

import jx.csp.model.Platform.TPlatformDetail;
import lib.ys.model.EVal;

/**
 * 投稿平台
 *
 * @auther Huoxuyu
 * @since 2017/9/28
 */
public class Platform extends EVal<TPlatformDetail> {

    public enum TPlatformDetail {
        id,            //单位号id
        nickname,    //单位号名称
        avatar,        //单位号头像
        info,        //单位号描述
    }
}

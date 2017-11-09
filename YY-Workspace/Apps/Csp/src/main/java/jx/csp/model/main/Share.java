package jx.csp.model.main;

import jx.csp.model.main.Share.TShare;
import lib.ys.model.EVal;

/**
 * @auther WangLan
 * @since 2017/10/13
 */

public class Share extends EVal<TShare> {

    public enum TShare {
        icon, // 分享平台图像
        name, // 分享平台名称
        type, // 分享类型
    }
}

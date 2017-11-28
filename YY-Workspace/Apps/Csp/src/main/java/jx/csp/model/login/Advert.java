package jx.csp.model.login;

import jx.csp.model.login.Advert.TAdvert;
import lib.ys.model.EVal;

/**
 * 广告
 *
 * @auther WangLan
 * @since 2017/9/26
 */

public class Advert extends EVal<TAdvert> {

    public enum TAdvert {
        imgUrl, //  广告图url
        id,
        countDown, // 倒计时
        pageUrl, // 广告内容url(链接)
    }

}

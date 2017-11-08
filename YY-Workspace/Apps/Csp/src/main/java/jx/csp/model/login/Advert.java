package jx.csp.model.login;

import jx.csp.model.login.Advert.TAdvert;
import lib.ys.model.EVal;

/**
 * @auther WangLan
 * @since 2017/9/26
 */

public class Advert extends EVal<TAdvert> {
    public enum TAdvert {
        imgUrl,
        id,
        countDown,
        pageUrl,
    }
}

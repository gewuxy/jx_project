package yaya.csp.model.login;

import lib.ys.model.EVal;
import yaya.csp.model.login.Advert.TAdvert;

/**
 * @auther WangLan
 * @since 2017/9/26
 */

public class Advert extends EVal<TAdvert>{
    public enum TAdvert {
        imgUrl,
        id,
        countDown,
        pageUrl,
    }
}

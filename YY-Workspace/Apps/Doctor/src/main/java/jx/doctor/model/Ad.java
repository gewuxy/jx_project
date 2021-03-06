package jx.doctor.model;

import lib.ys.model.EVal;
import jx.doctor.model.Ad.TAd;

/**
 * 广告类
 *
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Ad extends EVal<TAd> {

    public enum TAd {
        imageUrl,
        skipTime,
        pageUrl,
    }
}

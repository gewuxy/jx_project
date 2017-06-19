package yy.doctor.model.home;

import lib.ys.model.EVal;
import yy.doctor.model.home.Banner.TBanner;

/**
 * @author CaiXiang
 * @since 2017/5/8
 */
public class Banner extends EVal<TBanner> {

    public enum TBanner {
        id,    //广告id
        pageUrl,    //广告图片
        link,    //链接
        title, //标题
    }

}

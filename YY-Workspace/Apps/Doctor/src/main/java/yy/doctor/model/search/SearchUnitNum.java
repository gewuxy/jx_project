package yy.doctor.model.search;

import lib.ys.model.EVal;
import yy.doctor.model.search.SearchUnitNum.TSearchUnitNum;

/**
 * @author CaiXiang
 * @since 2017/5/8
 */
public class SearchUnitNum extends EVal<TSearchUnitNum> {

    public enum TSearchUnitNum {
        Id,             //公众号Id
        nickname,    //公众号昵称
        headimg,    //公众号头像
    }

}

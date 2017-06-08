package yy.doctor.model.search;

import lib.ys.model.EVal;
import yy.doctor.model.search.RecMeet.TRecUnitNum;

/**
 * @author CaiXiang
 * @since 2017/5/8
 */
public class RecMeet extends EVal<TRecUnitNum> {

    public enum TRecUnitNum {
        id, // 公众号Id
        attention, // 是否关注
        nickname, // 公众号昵称
        headimg, // 公众号头像
    }

}

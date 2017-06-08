package yy.doctor.model.unitnum;

import lib.ys.model.EVal;
import yy.doctor.model.unitnum.UnitNum.TUnitNum;

/**
 * @author CaiXiang
 * @since 2017/5/6
 */
public class UnitNum extends EVal<TUnitNum> {

    public enum TUnitNum {

        alpha,  //首字母
        headimg,    //公众号头像
        id,       //公众号id
        nickname,    //公众号昵称

    }
}

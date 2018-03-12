package jx.csp.model.contribution;

import jx.csp.model.contribution.UnitNum.TUnitNum;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class UnitNum extends EVal<TUnitNum> {

    public enum TUnitNum {
        id,
        name,

        search, // 搜索的关键字
    }
}

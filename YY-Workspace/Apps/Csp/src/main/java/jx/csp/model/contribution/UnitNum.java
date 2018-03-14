package jx.csp.model.contribution;

import jx.csp.model.contribution.UnitNum.TUnitNum;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class UnitNum extends EVal<TUnitNum> {

    public enum TUnitNum {
        id,  // 平台 id
        platformName,  // 平台名称
        imgUrl,  // 头像地址
        unitNumId,  // 单位号 id
    }
}

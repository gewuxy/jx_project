package jx.csp.model.contribution;

import jx.csp.model.contribution.HotUnitNum.THotUnitNum;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class HotUnitNum extends EVal<THotUnitNum> implements IContributeHistoryHotUnitNum {

    @Override
    public int getType() {
        return ContributeHistoryHotUnitNumType.hot_unit_num;
    }

    public enum THotUnitNum {
        id,
        name,
    }
}

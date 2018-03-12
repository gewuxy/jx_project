package jx.csp.model.contribution;

import jx.csp.model.contribution.LargeDivider.TLargeDivider;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class LargeDivider extends EVal<TLargeDivider> implements IContributeHistoryHotUnitNum {

    @Override
    public int getType() {
        return ContributeHistoryHotUnitNumType.large_divider;
    }

    public enum TLargeDivider {

    }
}

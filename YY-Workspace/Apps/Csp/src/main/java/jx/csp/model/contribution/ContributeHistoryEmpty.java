package jx.csp.model.contribution;

import jx.csp.model.contribution.ContributeHistoryEmpty.TContributeHistoryEmpty;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeHistoryEmpty extends EVal<TContributeHistoryEmpty> implements IContributeHistoryHotUnitNum {

    @Override
    public int getType() {
        return ContributeHistoryHotUnitNumType.contribution_history_empty;
    }

    public enum TContributeHistoryEmpty {

    }
}

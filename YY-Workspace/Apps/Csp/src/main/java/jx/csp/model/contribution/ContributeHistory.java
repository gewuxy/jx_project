package jx.csp.model.contribution;

import jx.csp.model.contribution.ContributeHistory.TContributeHistory;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeHistory extends EVal<TContributeHistory> implements IContributeHistoryHotUnitNum {

    @Override
    public int getType() {
        return ContributeHistoryHotUnitNumType.contribution_history;
    }

    public enum TContributeHistory {
        id,
        name,
    }
}

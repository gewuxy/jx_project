package jx.csp.model.contribution;

import java.util.List;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeHistories implements IContributeHistoryHotUnitNum {

    @Override
    public int getType() {
        return ContributeHistoryHotUnitNumType.contribution_history;
    }

    private List<ContributeHistory> mData;

    public List<ContributeHistory> getData() {
        return mData;
    }

    public void setData(List<ContributeHistory> data) {
        mData = data;
    }
}

package jx.csp.model.contribution;

import jx.csp.model.contribution.ListTitle.TListTitle;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ListTitle extends EVal<TListTitle> implements IContributeHistoryHotUnitNum {

    @Override
    public int getType() {
        return ContributeHistoryHotUnitNumType.list_title;
    }

    public enum TListTitle {
        title,
    }
}

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
        acceptId,  // 单位号id
        acceptName,  // 单位号名称
        acceptCount,  // 投稿次数
        headimg,  //  	单位号头像
        sign,  //
        type,  //  数据类型  0: 投稿历史 1: 热门单位号
    }
}

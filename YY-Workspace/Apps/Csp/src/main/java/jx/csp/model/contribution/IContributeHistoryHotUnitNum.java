package jx.csp.model.contribution;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public interface IContributeHistoryHotUnitNum {

    @IntDef({
            ContributeHistoryHotUnitNumType.list_title,
            ContributeHistoryHotUnitNumType.contribution_history,
            ContributeHistoryHotUnitNumType.contribution_history_empty,
            ContributeHistoryHotUnitNumType.large_divider,
            ContributeHistoryHotUnitNumType.hot_unit_num,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ContributeHistoryHotUnitNumType {
        int list_title = 0;
        int contribution_history = 1;
        int contribution_history_empty = 2;
        int large_divider = 3;
        int hot_unit_num = 4;
    }

    @ContributeHistoryHotUnitNumType
    int getType();

}

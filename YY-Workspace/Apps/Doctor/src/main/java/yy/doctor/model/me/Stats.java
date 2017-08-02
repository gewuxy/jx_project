package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.Stats.TStatistics;

/**
 * @auther : GuoXuan
 * @since : 2017/7/27
 */

public class Stats extends EVal<TStatistics> {

    public enum TStatistics {
        @Bind(asList = StatsPerDay.class)
        detailList, //  一周的数据

        totalCount, // 周数量
        unitCount, // 总数量
    }
}

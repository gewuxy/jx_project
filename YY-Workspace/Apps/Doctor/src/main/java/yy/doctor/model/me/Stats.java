package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.Stats.TStatistics;

/**
 * 每周的会议数
 *
 * @auther : GuoXuan
 * @since : 2017/7/27
 */

public class Stats extends EVal<TStatistics> {

    public enum TStatistics {
        @Bind(asList = StatsPerDay.class)
        detailList, //  一周的数据

        totalCount, // 总数量
        unitCount, // 周数量
    }
}

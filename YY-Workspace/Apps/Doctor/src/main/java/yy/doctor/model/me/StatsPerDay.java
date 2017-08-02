package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.StatsPerDay.TStatsPerDay;

/**
 * @auther : GuoXuan
 * @since : 2017/7/28
 */

public class StatsPerDay extends EVal<TStatsPerDay> {

    public enum TStatsPerDay {
        attendDate, // 日期
        count, // 数量(当天)
    }
}

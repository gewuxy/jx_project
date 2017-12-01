package jx.doctor.model.me;

import lib.ys.model.EVal;
import jx.doctor.model.me.StatsPerDay.TStatsPerDay;

/**
 * 每天的会议数
 *
 * @auther : GuoXuan
 * @since : 2017/7/28
 */

public class StatsPerDay extends EVal<TStatsPerDay> {

    public enum TStatsPerDay {
        attendTime, // 日期
        count, // 数量(当天)
    }
}

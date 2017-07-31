package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.Stats.TStatistics;

/**
 * @auther : GuoXuan
 * @since : 2017/7/27
 */

public class Stats extends EVal<TStatistics> {

    public enum TStatistics {
        color, // 柱状图的颜色
        title, // 标题
        time, // time
        index, // index

        @Bind(asList = StatsPerDay.class)
        list, // 一周的数据
    }
}

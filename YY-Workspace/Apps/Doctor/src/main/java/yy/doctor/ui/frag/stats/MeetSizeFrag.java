package yy.doctor.ui.frag.stats;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.ys.util.TimeUtil;
import lib.ys.util.res.ResLoader;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.me.Stats;
import yy.doctor.model.me.Stats.TStatistics;
import yy.doctor.model.me.StatsPerDay;
import yy.doctor.model.me.StatsPerDay.TStatsPerDay;

/**
 * @auther : GuoXuan
 * @since : 2017/7/28
 */

public class MeetSizeFrag extends BaseSizeFrag {

    private ArrayList<Stats> mStatses;

    @Override
    public void initData() {
        long curr = System.currentTimeMillis();
        mStatses = new ArrayList<>();
        for (int i = 0; i < KSize * 2; i++) {
            Stats stats = new Stats();
            stats.put(TStatistics.index, i);
            stats.put(TStatistics.color, ResLoader.getColor(getColor()));
            stats.put(TStatistics.time, curr + (i - KSize) * TimeUnit.DAYS.toMillis(7));
            mStatses.add(stats);

            Bundle b = new Bundle();
            b.putSerializable(Extra.KData, mStatses);

            HistogramFrag frag = new HistogramFrag();
            frag.setArguments(b);
            add(frag);
        }
    }

    @Override
    protected String getTitle() {
        return "参会数量";
    }

    @Override
    protected int getColor() {
        return R.color.text_0882e7;
    }

}

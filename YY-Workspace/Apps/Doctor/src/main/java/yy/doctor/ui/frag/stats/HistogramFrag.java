package yy.doctor.ui.frag.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeUtil;
import lib.yy.network.Result;
import lib.yy.ui.frag.base.BaseFrag;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.me.Stats;
import yy.doctor.model.me.Stats.TStatistics;
import yy.doctor.model.me.StatsPerDay;
import yy.doctor.model.me.StatsPerDay.TStatsPerDay;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.view.HistogramView;

/**
 * @auther : GuoXuan
 * @since : 2017/7/27
 */
public class HistogramFrag extends BaseFrag {

    private HistogramView mLayoutHistogram;

    private ArrayList<Stats>  mStats;
    private int mIndex;

    @Override
    public void initData() {
        Bundle b = getArguments();
        if (b != null) {
            mIndex = b.getInt(Extra.KId);
            mStats = (ArrayList<Stats>) b.getSerializable(Extra.KData);
        }
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_histogram;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mLayoutHistogram = findView(R.id.layout_histogram_view);
    }

    @Override
    public void setViews() {
        setBackgroundColor(Color.TRANSPARENT);

        Stats stats = mStats.get(mIndex);
        mLayoutHistogram.setRecColor(stats.getInt(TStatistics.color));

        List<StatsPerDay> statsPerDays = stats.getList(TStatistics.list);
        if (statsPerDays == null) {
            setViewState(ViewState.loading);
            exeNetworkReq(NetFactory.banner());
        } else {
            mLayoutHistogram.setMeets(stats);
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            setViewState(ViewState.normal);

            Stats stats = mStats.get(mIndex);
            long time = stats.getLong(TStatistics.time);
            List<StatsPerDay> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                StatsPerDay statsPerDay = new StatsPerDay();
                statsPerDay.put(TStatsPerDay.date, TimeUtil.formatMilli(time + TimeUnit.DAYS.toMillis(i), "MM月dd日"));
                statsPerDay.put(TStatsPerDay.num, (time + i) % 10);
                week.add(statsPerDay);
            }
            stats.put(TStatistics.list, week);
            mLayoutHistogram.setMeets(stats);
        }

    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

}

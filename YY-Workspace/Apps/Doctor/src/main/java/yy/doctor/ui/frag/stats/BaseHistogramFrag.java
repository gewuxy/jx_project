package yy.doctor.ui.frag.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkReq;
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
 * @since : 2017/8/2
 */

public abstract class BaseHistogramFrag extends BaseFrag {

    private HistogramView mLayoutHistogram;

    private ArrayList<Stats> mStats;
    private int mIndex;

    public Stats getStats() {
        return mStats.get(mIndex);
    }

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

        mLayoutHistogram.setRecColor(getColor());

        Stats stats = mStats.get(mIndex);
        List<StatsPerDay> statsPerDays = stats.getList(TStatistics.detailList);
        if (statsPerDays == null) {
            setViewState(ViewState.loading);
            exeNetworkReq(getNetReq());
        } else {
            mLayoutHistogram.setStats(stats);
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

            /*Stats stats = mStats.get(mIndex);
            long time = stats.getLong(TStatistics.time);
            List<StatsPerDay> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                StatsPerDay statsPerDay = new StatsPerDay();
                statsPerDay.put(TStatsPerDay.date, TimeUtil.formatMilli(time + TimeUnit.DAYS.toMillis(i), "MM月dd日"));
                statsPerDay.put(TStatsPerDay.num, (time + i) % 10);
                week.add(statsPerDay);
            }
            stats.put(TStatistics.list, week);
            mLayoutHistogram.setStats(stats);*/
        } else {
            showToast(r.getError());
        }

    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    public int getOffset() {
        if (mStats == null) {
            return 0;
        }
        return mStats.size() - mIndex;
    }

    public String getCount() {
        if (getStats() == null) {
            return "0";
        }
        return getStats().getString(TStatistics.unitCount);
    }

    @ColorRes
    protected abstract int getColor();

    protected abstract NetworkReq getNetReq();

}

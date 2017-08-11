package yy.doctor.ui.frag.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.frag.base.BaseFrag;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.me.Stats;
import yy.doctor.model.me.Stats.TStatistics;
import yy.doctor.model.me.StatsPerDay;
import yy.doctor.network.JsonParser;
import yy.doctor.view.HistogramView;

/**
 * @auther : GuoXuan
 * @since : 2017/8/2
 */

public abstract class BaseHistogramFrag extends BaseFrag {

    private HistogramView mLayoutHistogram;
    private OnStatsChangeListener mListener;

    private ArrayList<Stats> mStats;
    private int mIndex;

    public interface OnStatsChangeListener {
        void onChange(int index, String weekCount, String allCount);
    }

    public void setStatsChangeListener(OnStatsChangeListener listener) {
        mListener = listener;
    }

    public Stats getStats() {
        if (mStats == null) {
            return null;
        }
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
            mLayoutHistogram.setStats(statsPerDays);
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Stats.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Stats> r = (Result<Stats>) result;
        if (r.isSucceed()) {
            Stats stats = r.getData();
            List<StatsPerDay> week = stats.getList(TStatistics.detailList);
            mStats.set(mIndex, stats);
            mLayoutHistogram.setStats(week);
            if (mListener != null) {
                mListener.onChange(mIndex,
                        stats.getString(TStatistics.unitCount),
                        stats.getString(TStatistics.totalCount));
            }
            setViewState(ViewState.normal);
        } else {
            showToast(r.getMessage());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public boolean onRetryClick() {
        exeNetworkReq(getNetReq());
        return super.onRetryClick();
    }

    public int getOffset() {
        if (mStats == null) {
            return 0;
        }
        return mStats.size() - mIndex - 1;
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

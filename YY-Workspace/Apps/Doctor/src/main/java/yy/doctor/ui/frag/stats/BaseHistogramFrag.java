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
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
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

abstract public class BaseHistogramFrag extends BaseFrag {

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
        return mStats.get(mIndex);
    }

    @Override
    public void initData(Bundle state) {
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
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Stats.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            Stats stats = (Stats) r.getData();
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
        if (!super.onRetryClick()) {
            setViewState(ViewState.loading);
            exeNetworkReq(getNetReq());
        }
        return true;
    }

    public int getOffset() {
        return mStats.size() - mIndex - 1;
    }

    public String getCount() {
        //网络数据还没有返回时
        if (getStats() == null) {
            return "0";
        }
        return getStats().getString(TStatistics.unitCount);
    }

    @ColorRes
    abstract protected int getColor();

    abstract protected NetworkReq  getNetReq();

}

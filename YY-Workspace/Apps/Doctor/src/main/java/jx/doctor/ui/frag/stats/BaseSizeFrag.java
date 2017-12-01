package jx.doctor.ui.frag.stats;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.frag.base.BaseVPFrag;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.model.me.Stats;
import jx.doctor.ui.frag.stats.BaseHistogramFrag.OnStatsChangeListener;

/**
 * @auther : GuoXuan
 * @since : 2017/7/28
 */

abstract public class BaseSizeFrag extends BaseVPFrag implements OnPageChangeListener, OnStatsChangeListener {

    protected final int KSize = 1000;

    private TextView mTvTitle;
    private TextView mTvWeek;
    private TextView mTvAll;

    private ArrayList<Stats> mStatses;

    @Override
    public void initData(Bundle state) {
        mStatses = new ArrayList<>();
        for (int i = 0; i < KSize; i++) {
            Stats stats = new Stats();
            mStatses.add(stats);

            Bundle b = new Bundle();

            b.putSerializable(Extra.KData, mStatses);
            b.putSerializable(Extra.KId, i);

            BaseHistogramFrag frag = getFragment();
            frag.setArguments(b);
            frag.setStatsChangeListener(this);
            add(frag);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.frag_statistics;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTitle = findView(R.id.statistics_meet_tv_title);
        mTvWeek = findView(R.id.statistics_meet_tv_num_week);
        mTvAll = findView(R.id.statistics_meet_tv_num_all);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvTitle.setText(getTitle());
        mTvWeek.setTextColor(ResLoader.getColor(getTextColor()));
        mTvAll.setTextColor(ResLoader.getColor(getTextColor()));
        setCurrPosition(KSize);
        setOffscreenPageLimit(3);
        setOnPageChangeListener(this);
    }

    @Override
    protected BaseHistogramFrag getItem(int position) {
        return (BaseHistogramFrag) super.getItem(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mTvWeek.setText(getItem(position).getCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onChange(int index, String weekCount, String allCount) {
        if (getCurrPosition() == index) {
            // 当前页
            mTvWeek.setText(weekCount);
        }
        if (getCurrPosition() == KSize - 1) {
            // 不会再变, 不多次设置
            mTvAll.setText(allCount);
        }
    }

    abstract protected String getTitle();

    abstract protected BaseHistogramFrag getFragment();

    @ColorRes
    abstract protected int getTextColor();
}

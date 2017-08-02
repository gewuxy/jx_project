package yy.doctor.ui.frag.stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseVPFrag;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.me.Stats;
import yy.doctor.model.me.Stats.TStatistics;

/**
 * @auther : GuoXuan
 * @since : 2017/7/28
 */

abstract public class BaseSizeFrag extends BaseVPFrag implements OnPageChangeListener {

    protected final int KSize = 1000;

    private TextView mTvTitle;
    private TextView mTvWeek;
    private TextView mTvAll;

    private ArrayList<Stats> mStatses;

    @Override
    public void initData() {
        mStatses = new ArrayList<>();
        for (int i = 0; i < KSize; i++) {
            Stats stats = new Stats();
            mStatses.add(stats);

            Bundle b = new Bundle();
            b.putSerializable(Extra.KData, mStatses);
            b.putSerializable(Extra.KId, i);

            BaseHistogramFrag frag = getFragment();
            frag.setArguments(b);
            add(frag);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_statistics;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        super.findViews();

        setOnPageChangeListener(this);
        mTvTitle = findView(R.id.statistics_meet_tv_title);
        mTvWeek = findView(R.id.statistics_meet_tv_num_week);
        mTvAll = findView(R.id.statistics_meet_tv_num_all);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvTitle.setText(getTitle());
        setCurrentItem(KSize);
        setOffscreenPageLimit(3);
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
    protected BaseHistogramFrag getItem(int position) {
        return (BaseHistogramFrag) super.getItem(position);
    }

    abstract protected String getTitle();

    protected abstract BaseHistogramFrag getFragment();

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        super.onNotify(type, data);
    }
}

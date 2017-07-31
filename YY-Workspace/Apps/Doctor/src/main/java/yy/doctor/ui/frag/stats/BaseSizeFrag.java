package yy.doctor.ui.frag.stats;

import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseVPFrag;
import yy.doctor.R;

/**
 * @auther : GuoXuan
 * @since : 2017/7/28
 */

abstract public class BaseSizeFrag extends BaseVPFrag  {

    private TextView mTvTitle;
    protected final int KSize = 500;

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

        mTvTitle = findView(R.id.statistics_meet_tv_title);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvTitle.setText(getTitle());
        setCurrentItem(KSize);
        setOffscreenPageLimit(3);
    }

    @Override
    protected HistogramFrag getItem(int position) {
        return (HistogramFrag) super.getItem(position);
    }

    abstract protected String getTitle();

    abstract protected int getColor();

}

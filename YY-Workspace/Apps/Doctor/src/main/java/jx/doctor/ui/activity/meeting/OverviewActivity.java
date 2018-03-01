package jx.doctor.ui.activity.meeting;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;

import java.util.ArrayList;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.doctor.R;
import jx.doctor.adapter.meeting.OverviewAdapter;
import jx.doctor.util.Util;
import lib.jx.ui.activity.base.BaseRecyclerActivity;
import lib.ys.ui.other.NavBar;

/**
 * 全览页面
 *
 * @author CaiXiang
 * @since 2018/3/1
 */
@Route
public class OverviewActivity extends BaseRecyclerActivity<String, OverviewAdapter> {

    @Arg
    String mTitle;

    @Arg
    ArrayList<String> mList;

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mTitle, this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_overview;
    }

    @Override
    protected LayoutManager initLayoutManager() {
        return new GridLayoutManager(this, 2);
    }

    @Override
    public void setViews() {
        super.setViews();

        setData(mList);
    }
}

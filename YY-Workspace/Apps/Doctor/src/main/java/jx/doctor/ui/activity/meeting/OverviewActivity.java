package jx.doctor.ui.activity.meeting;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import java.util.ArrayList;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.adapter.meeting.OverviewAdapter;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.util.Util;
import lib.jx.ui.activity.base.BaseRecyclerActivity;
import lib.ys.adapter.recycler.OnRecyclerItemClickListener;
import lib.ys.ui.other.NavBar;

/**
 * 全览页面
 *
 * @author CaiXiang
 * @since 2018/3/1
 */
@Route
public class OverviewActivity extends BaseRecyclerActivity<Course, OverviewAdapter> {

    @Arg
    String mTitle;

    @Arg
    ArrayList<Course> mList;

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
        getAdapter().setOnItemClickListener(new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent().putExtra(Extra.KData, position);
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }
}

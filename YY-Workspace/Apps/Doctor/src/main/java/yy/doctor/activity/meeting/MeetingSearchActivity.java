package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import lib.ys.adapter.interfaces.OnRecyclerItemClickListener;
import lib.ys.ex.NavBar;
import lib.ys.fitter.DpFitter;
import lib.yy.activity.base.BaseActivity;
import lib.yy.view.GridDivider;
import yy.doctor.Constants.SectionConstants;
import yy.doctor.R;
import yy.doctor.adapter.SectionAdapter;
import yy.doctor.util.Util;

/**
 * 搜索会议界面
 *
 * @author : GuoXuan
 * @since : 2017/4/26
 */

public class MeetingSearchActivity extends BaseActivity {

    private RecyclerView mRv;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_search;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View v = inflate(R.layout.layout_home_nav_bar);
        bar.addViewLeft(v, null);
    }

    @Override
    public void findViews() {
        mRv = findView(R.id.meeting_search_recyclerview);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.meeting_search_tv_number);
        setOnClickListener(R.id.meeting_search_tv_meeting);

        mRv.setLayoutManager(new StaggeredGridLayoutManager(SectionConstants.KRowCount, StaggeredGridLayoutManager.VERTICAL));
        mRv.addItemDecoration(new GridDivider(
                DpFitter.dp(SectionConstants.KDividerHeight),
                R.drawable.section_divider_bg));

        final SectionAdapter adapter = new SectionAdapter();
        adapter.addAll(Util.getSections());
        mRv.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                showToast(adapter.getItem(position));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_search_tv_number:
                showToast("公众号");
                break;
            case R.id.meeting_search_tv_meeting:
                showToast("会议");
                break;
            default:
                break;
        }

    }
}
